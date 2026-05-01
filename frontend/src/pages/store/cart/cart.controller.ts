import type { ICartItem } from "@interfaces/ICartItem";
import type { Product } from "@interfaces/Product";
import { cartService } from "@services/cartService";
import { productService } from "@services/productService";
import { updateCartBadge } from "@utils/components";
import { navigate } from "@utils/navigate";
import { PATHS } from "@utils/paths";
import { storage } from "@utils/storage";
import {
    updateProductImageUI,
    formattedPriceHTML,
    wrapWithDetailLink,
} from "@utils/uiUtils";

/**
 * Función para cargar los productos del carrito en el contenedor principal
 * @param products lista completa de productos
 */
export const showCart = (products: Product[]): void => {
    const cartProductContainer = document.querySelector<HTMLElement>(
        "#cart-product-container",
    );

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!cartProductContainer) return;

    // Filtramos los productos activos
    const activeProducts: Product[] =
        productService.getActiveProducts(products);

    // Filtramos los productos del carrito
    const cartItems: Product[] = cartService.getCartItems(activeProducts);

    // Limpiar el contenedor de productos
    cartProductContainer ? (cartProductContainer.innerHTML = "") : null;

    // Si hay productos en el carrito, los mostramos. De lo contrario,
    // mostramos mensaje de carrito vacío.
    if (cartItems.length > 0) {
        cartItems.forEach((prod: Product): void => {
            const article: HTMLElement = document.createElement("article");
            article.classList.add("card", "cart__product");
            article.dataset.id = prod.id.toString();
            article.dataset.price = prod.precio.toString();

            const linkedName = wrapWithDetailLink(
                prod.id,
                `<h3 class="cart__product-title">${prod.nombre}</h3>`,
            );

            const unitPrice: string = formattedPriceHTML(prod.precio);

            article.innerHTML = `
            <div class="cart__product-info">
                <img class="cart__product-img" src="" id="img-product-${prod.id}" alt="${prod.nombre}">
                <div class="cart__product-details">
                    ${linkedName}
                    <p class="cart__product-description">${prod.descripcion}</p>
                    <p class="price cart__product-price">${unitPrice}<span>c/u</span></p>
                </div>
              </div>
              <div class="cart__product-btns">
                <button class="btn btn--square btn--minus">-</button>
                <input type="number" name="quantity" class="product-qty" value="" min="1">
                <button class="btn btn--square btn--plus">+</button>
                <p class="price cart__product-price cart__product-price--subtotal"></p>
                <button class="btn btn--square btn--trash">🗑</button>
              </div>
            `;
            cartProductContainer?.appendChild(article);

            // Iniciamos la carga asíncrona de la imagen
            updateProductImageUI(prod.id);

            // Obtener la cantidad actual desde el storage para este producto
            const currentQty: number = cartService.getProductQuantity(prod.id);
            const inputQty =
                article.querySelector<HTMLInputElement>(".product-qty");

            // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
            if (!inputQty) return;

            // Establecer el máximo en base al stock disponible
            inputQty.value = currentQty.toString();
            inputQty.addEventListener("change", (): void => {
                const newQty: number = parseInt(inputQty.value, 10) || 1;
                updateCartItemUI(
                    prod.id,
                    newQty,
                    products,
                    cartProductContainer,
                );
            });

            // Calcular subtotal de este producto
            const subtotalValue: number = prod.precio * currentQty;
            const subtotalPrice: string = formattedPriceHTML(subtotalValue);
            const subtotalElement = article.querySelector<HTMLParagraphElement>(
                ".cart__product-price--subtotal",
            );

            // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
            if (!subtotalElement) return;

            subtotalElement.innerHTML = subtotalPrice;

            // Evento de los botones +, - y eliminar
            const btnMinus =
                article.querySelector<HTMLButtonElement>(".btn--minus");
            const btnPlus =
                article.querySelector<HTMLButtonElement>(".btn--plus");
            const btnTrash =
                article.querySelector<HTMLButtonElement>(".btn--trash");

            // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
            if (!btnMinus || !btnPlus || !btnTrash) return;

            btnMinus.addEventListener("click", (): void => {
                const currentQty = parseInt(inputQty.value, 10) || 1;
                updateCartItemUI(
                    prod.id,
                    currentQty - 1,
                    products,
                    cartProductContainer,
                );
            });
            btnPlus.addEventListener("click", (e: Event): void => {
                e.preventDefault();

                const currentQty = parseInt(inputQty.value, 10) || 1;
                updateCartItemUI(
                    prod.id,
                    currentQty + 1,
                    products,
                    cartProductContainer,
                );
            });
            btnTrash.addEventListener("click", (): void => {
                if (confirm(`¿Eliminar ${prod.nombre} del carrito?`)) {
                    updateCartItemUI(
                        prod.id,
                        0,
                        products,
                        cartProductContainer,
                    );
                }
            });
        });
    } else {
        const cartEmpty = document.createElement("div") as HTMLDivElement;
        const emptyResult = document.createElement("p") as HTMLParagraphElement;
        const btnReturnStore = document.createElement(
            "button",
        ) as HTMLButtonElement;

        cartEmpty.classList.add("cart__empty");
        emptyResult.classList.add("empty-result");
        emptyResult.textContent = "No se encontraron productos en el carrito.";
        btnReturnStore.classList.add("btn", "btn--primary");
        btnReturnStore.textContent = "Volver a la tienda";

        cartEmpty?.appendChild(emptyResult);
        cartEmpty?.appendChild(btnReturnStore);
        cartProductContainer?.appendChild(cartEmpty);

        btnReturnStore.addEventListener("click", (): void => {
            navigate(PATHS.STORE.HOME);
        });
    }
    // Actualizamos el resumen del carrito
    updateCartSummary(cartItems);

    // Actualizamos el badge del carrito
    updateCartBadge();
};

/**
 * Función para actualizar la interfaz de un producto específico en el carrito
 * cuando se cambia su cantidad o se elimina sin necesidad de recargar toda la vista.
 * @param id ID del producto a actualizar
 * @param newQty Nueva cantidad del producto (si es 0 o menor, se eliminará del carrito)
 * @param products lista completa de productos para poder recalcular el resumen
 * del carrito después de la actualización
 * @param cartContainer El elemento HTML que contiene los productos del carrito
 */
export const updateCartItemUI = (
    id: number,
    newQty: number,
    products: Product[],
    cartContainer: HTMLElement,
): void => {
    // 1. Buscamos la "fila" específica por su dataset id
    const itemContainer = cartContainer.querySelector<HTMLElement>(
        `[data-id="${id}"]`,
    );

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!itemContainer) return;

    if (newQty <= 0) {
        // Eliminar del DOM y storage
        storage.removeCartItem(id);
        itemContainer.remove();

        // Verificamos si el carrito quedó vacío
        const cartItems: ICartItem[] = storage.getCartItems();
        if (cartItems.length === 0) {
            showCart(products);
            return; // Cortamos el flujo, el propio showCart limpiará todo el DOM
        }
    } else {
        const input =
            itemContainer.querySelector<HTMLInputElement>(".product-qty");

        // Actualizamos storage y verificamos stock
        const success: boolean = storage.updateCartItem(id, newQty);
        if (!success) {
            alert("No hay suficiente stock disponible.");

            // Revertir input a la cantidad real existente en el carrito
            const currentQty: number = cartService.getProductQuantity(id);
            if (input) input.value = currentQty.toString();
            return;
        }

        // 2. Actualizamos el valor del input sin renderizar nada más
        if (input) input.value = newQty.toString();

        // 3. Calculamos el nuevo subtotal con data attributes
        const price: number = Number(itemContainer.dataset.price) || 0;
        const subtotalElement =
            itemContainer.querySelector<HTMLParagraphElement>(
                ".cart__product-price--subtotal",
            );
        if (subtotalElement) {
            const nuevoSubtotal: number = price * newQty;
            subtotalElement.innerHTML = formattedPriceHTML(nuevoSubtotal);
        }
    }

    // 4. Actualizamos el resumen total de forma dinámica
    const activeProducts: Product[] =
        productService.getActiveProducts(products);
    const updatedCartItems: Product[] =
        cartService.getCartItems(activeProducts);
    updateCartSummary(updatedCartItems);

    // Y actualizamos el badge de la barra de navegación superior
    updateCartBadge();
};

/**
 * Calcula el subtotal del carrito
 * @param items lista de productos en el carrito
 * @returns el monto subtotal
 */
const getSubtotal = (items: Product[]): number => {
    const cartData: ICartItem[] = storage.getCartItems();
    return items.reduce((acc: number, prod: Product): number => {
        const qty: number =
            cartData.find((i: ICartItem): boolean => i.id === prod.id)?.qty ||
            0;
        return acc + prod.precio * qty;
    }, 0);
};

/**
 * Actualiza un elemento del DOM con un monto formateado
 * @param selector selector del elemento DOM
 * @param amount monto a renderizar
 */
const renderAmount = (selector: string, amount: number): void => {
    const element = document.querySelector<HTMLElement>(selector);
    if (element) {
        element.innerHTML = formattedPriceHTML(amount);
    }
};

/**
 * Función para actualizar el resumen del carrito (subtotal, envío, total)
 * @param items lista de productos actualmente en el carrito
 */
const updateCartSummary = (items: Product[]): void => {
    const subtotal: number = getSubtotal(items);
    const shippingCost: number = subtotal > 0 ? 500 : 0;

    renderAmount("#cart-subtotal-amount", subtotal);
    renderAmount("#cart-shipping-amount", shippingCost);
    renderAmount("#cart-total-amount", subtotal + shippingCost);
};

/**
 * Función para inicializar los eventos del carrito
 * @param products lista completa de productos
 */
export const initCartEvents = (products: Product[]): void => {
    const btnClearCart =
        document.querySelector<HTMLButtonElement>("#btn-clear-cart");
    const btnCheckout =
        document.querySelector<HTMLButtonElement>("#btn-checkout");

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!btnClearCart || !btnCheckout) return;

    // Evento del botón "Vaciar carrito"
    if (btnClearCart) {
        btnClearCart.addEventListener("click", (): void => {
            // Ahora el confirm solo se disparará una vez
            if (confirm("¿Desea vaciar el carrito?")) {
                storage.clearCart();
                // Refrescamos la vista pasando la lista de productos
                showCart(products);
            }
        });
    }

    // Evento del botón "Checkout"
    if (btnCheckout) {
        btnCheckout.addEventListener("click", (): void => {
            alert("Checkout Próximamente");
        });
    }
};
