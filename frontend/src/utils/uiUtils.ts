import { PATHS } from "@utils/paths";
import { productService } from "@services/productService";
import { cartService } from "@/services/cartService";
import type { Product } from "@/types/Product";

/**
 * Busca la imagen de un producto aleatorio en la API y la asigna al elemento
 * <img> correspondiente.
 * @param id ID del producto para localizar el elemento en el DOM.
 */
export const updateProductImageUI = (id: number): void => {
    // Obtener URL de la imagen desde el servicio
    const url: string = productService.getPersistentImage(id);

    // Actualizar el src del elemento <img> con el ID específico
    const imgElement = document.querySelector<HTMLImageElement>(
        `#img-product-${id}`,
    );

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!imgElement) return;

    if (imgElement) {
        imgElement.src = url;
    }
};

/**
 * Formatea un precio numérico a un string con formato de moneda argentina, y lo
 * divide en parte entera y decimal.
 * @param price Precio a formatear.
 * @returns Un HTML string con la parte entera y decimal envueltas en spans para
 * estilos separados.
 */
export const formattedPriceHTML = (price: number): string => {
    // Formatear el precio a moneda argentina
    const priceString: string = price.toLocaleString("es-AR", {
        style: "currency",
        currency: "ARS",
    });

    // Dividir el precio en parte entera y decimal
    const [wholePrice, decimalPrice]: string[] = priceString.split(",");
    return `
    <span class="price--whole">${wholePrice}</span><span class="price--decimal">${decimalPrice}</span>
    `;
};

/**
 * Envuelve un fragmento de HTML con un enlace al detalle del producto.
 * @param id ID del producto.
 * @param content Contenido HTML a envolver.
 * @returns String HTML del enlace.
 */
export const wrapWithDetailLink = (id: number, content: string): string => {
    return `<a href="${PATHS.STORE.DETAIL(id)}" class="link">${content}</a>`;
};

/**
 * Genera el atributo 'disabled' o una clase CSS para indicar que un control no está disponible.
 * @param isAvailable Booleano que indica si el control debe estar activo.
 * @param classSelector Opcional. Si se proporciona, se devuelve la clase CSS en
 * lugar del atributo 'disabled'.
 * @returns Un string con el atributo 'disabled' y/o la clase CSS.
 */
export const getDisabledState = (
    isAvailable: boolean,
    classSelector?: string,
): string => {
    // Si no está disponible y se pasó una clase CSS, devolvemos esa clase.
    if (!isAvailable && classSelector) {
        return classSelector;
    }

    // Si no, devolvemos "disabled" como atributo.
    return !isAvailable ? "disabled" : "";
};

/**
 * Crea y muestra un aviso visual cuando se añade un producto al carrito
 * @param parentElement Elemento de referencia en el DOM
 * @param qty Cantidad añadida
 * @param productName Nombre del producto añadido
 * @param position 'after' inserta el aviso después del elemento, 'append' lo inserta como último hijo
 */
export const showCartNotice = (
    parentElement: HTMLElement,
    qty: number,
    productName: string,
    position: "after" | "append" = "after",
): void => {
    // Eliminamos la notificación anterior si existe para no acumularlas
    document.querySelector(".cart-notice")?.remove();

    const notice: HTMLDivElement = document.createElement("div");
    notice.classList.add("card", "cart-notice", "cart-notice--slide-in");
    notice.setAttribute("role", "alert");

    if (position === "after") {
        notice.innerHTML = `
        <p>Se añadió "${productName}" a tu carrito.</p>
        <a href="${PATHS.STORE.CART}" class="btn btn--primary">Ver carrito</a>
        `;

        // Insertamos el aviso después del elemento padre
        parentElement.after(notice);
    } else {
        notice.innerHTML = `
        <p>${qty} &times; "${productName}" ${qty > 1 ? "han sido añadidos" : "ha sido añadido"} a tu carrito.</p>
        <a href="${PATHS.STORE.CART}" class="btn btn--primary">Ver carrito</a>
        `;

        // Nos aseguramos que el padre pueda contener un elemento con posición absoluta
        if (window.getComputedStyle(parentElement).position === "static") {
            parentElement.style.position = "relative";
        }

        // Insertamos el aviso como último hijo del elemento padre
        parentElement.appendChild(notice);
    }

    // Removemos automáticamente después de 5 segundos
    setTimeout(() => {
        if (document.body.contains(notice)) {
            // En lugar de removerlo de inmediato, disparamos la animación de salida
            notice.classList.replace(
                "cart-notice--slide-in",
                "cart-notice--slide-out",
            );

            // Esperamos a que la animación termine para quitarlo del DOM
            setTimeout((): void => {
                if (document.body.contains(notice)) notice.remove();
            }, 300);
        }
    }, 5000);
};

/**
 * Calcula la disponibilidad de un producto restando lo que ya está en el carrito.
 * del stock total.
 * @param product El producto del cual se desea calcular la disponibilidad.
 * @return Un objeto con la cantidad disponible restante, un booleano que indica si el
 * producto está disponible para agregar al carrito y la cantidad actual en el carrito.
 */
export const getItemAvailability = (
    product: Product,
): {
    available: number;
    isAvailable: boolean;
    qtyInCart: number;
} => {
    const qtyInCart = cartService.getProductQuantity(product.id);
    const available: number = product.stock - qtyInCart;
    const isAvailable: boolean = product.disponible && available > 0;

    return { available, isAvailable, qtyInCart };
};

/**
 * Helper que actualiza la interfaz de usuario para reflejar el estado de stock y disponibilidad
 * @param container El contenedor HTML donde se encuentran los elementos a actualizar.
 * @param isAvailable Un booleano que indica si el producto está disponible para agregar al carrito.
 * @param selectors Un objeto con los selectores CSS para el badge de stock y el botón de agregar al carrito.
 */
export const updateBaseAvailabilityUI = (
    container: HTMLElement,
    isAvailable: boolean,
    selectors: { badge: string; button: string },
): void => {
    const badge = container.querySelector<HTMLElement>(selectors.badge);
    const btnAdd = container.querySelector<HTMLButtonElement>(selectors.button);

    if (badge) {
        badge.classList.toggle("stock-badge--out-of-stock", !isAvailable);
    }

    if (btnAdd) {
        btnAdd.disabled = !isAvailable;
        btnAdd.textContent = isAvailable
            ? "Agregar al carrito"
            : "No disponible";
    }
};
