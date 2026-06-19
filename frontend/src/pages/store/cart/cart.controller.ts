import type { ICartItem } from "@interfaces/ICartItem";
import type { IProduct } from "@interfaces/Product.interface";
import { cartService } from "@services/cart.service";
import { productService } from "@services/product.service";
import { updateCartBadge } from "@utils/components";
import { navigate } from "@utils/navigate";
import { PATHS } from "@utils/paths";
import { storage } from "@utils/storage";
import { formattedPriceHTML, wrapWithDetailLink } from "@utils/uiUtils";

/**
 * Función para cargar los productos del carrito en el contenedor principal
 */
export const showCart = async (): Promise<void> => {
    const cartProductContainer = document.querySelector<HTMLElement>("#cart-product-container");
    if (!cartProductContainer) return;

    // Creamos un fragmento para optimizar la inserción de múltiples elementos en el DOM
    const fragment: DocumentFragment = document.createDocumentFragment();

    // Filtramos los productos activos
    const activeProducts: IProduct[] = await productService.getAll();

    // Filtramos los productos del carrito
    const cartItems: IProduct[] = cartService.getCartItems(activeProducts);

    // Limpiar el contenedor de productos
    cartProductContainer.innerHTML = "";

    // Si hay productos en el carrito, los mostramos. De lo contrario,
    // mostramos mensaje de carrito vacío.
    if (cartItems.length > 0) {
        cartItems.forEach((prod: IProduct): void => {
            const article: HTMLElement = document.createElement("article");
            article.classList.add("card", "cart__product");
            article.dataset.id = prod.id.toString();
            article.dataset.price = prod.price.toString();

            const linkedName = wrapWithDetailLink(prod.id, `<h3 class="cart__product-title">${prod.name}</h3>`);

            const unitPrice: string = formattedPriceHTML(prod.price);

            article.innerHTML = `
                <div class="cart__product-info">
                    <img class="cart__product-img" src="${prod.imageUrl}" id="img-product-${prod.id}" alt="${prod.name}">
                    <div class="cart__product-details">
                        ${linkedName}
                        <p class="cart__product-description">${prod.description}</p>
                        <p class="price cart__product-price">${unitPrice}<span>c/u</span></p>
                    </div>
                </div>
                <div class="cart__product-btns">
                    <section>
                        <button class="btn btn--square btn--minus">-</button>
                        <input type="number" name="quantity" class="product-qty" value="" min="1">
                        <button class="btn btn--square btn--plus">+</button>
                    </section>
                    <section>
                        <p class="price cart__product-price cart__product-price--subtotal"></p>
                    </section>
                    <section>
                        <button class="btn btn--square btn--trash">🗑</button>
                    </section>
                </div>
            `;
            fragment.appendChild(article);

            // Obtener la cantidad actual desde el storage para este producto
            const currentQty: number = cartService.getProductQuantity(prod.id);
            const inputQty = article.querySelector<HTMLInputElement>(".product-qty");
            if (!inputQty) return;

            // Establecer el máximo en base al stock disponible
            inputQty.value = currentQty.toString();
            inputQty.addEventListener("change", async (): Promise<void> => {
                const newQty: number = parseInt(inputQty.value, 10) || 1;
                await updateCartItemUI(prod.id, newQty, cartProductContainer);
            });

            // Calcular subtotal de este producto
            const subtotalValue: number = prod.price * currentQty;
            const subtotalPrice: string = formattedPriceHTML(subtotalValue);
            const subtotalElement = article.querySelector<HTMLParagraphElement>(".cart__product-price--subtotal");
            if (!subtotalElement) return;

            subtotalElement.innerHTML = subtotalPrice;

            // Evento de los botones +, - y eliminar
            const btnMinus = article.querySelector<HTMLButtonElement>(".btn--minus");
            const btnPlus = article.querySelector<HTMLButtonElement>(".btn--plus");
            const btnTrash = article.querySelector<HTMLButtonElement>(".btn--trash");
            if (!btnMinus || !btnPlus || !btnTrash) return;

            // Manejadores asíncronos con await para desenvolver las promesas del storage
            btnMinus.addEventListener("click", async (): Promise<void> => {
                const currentQty = parseInt(inputQty.value, 10) || 1;
                await updateCartItemUI(prod.id, currentQty - 1, cartProductContainer);
            });

            btnPlus.addEventListener("click", async (e: Event): Promise<void> => {
                e.preventDefault();
                const currentQty = parseInt(inputQty.value, 10) || 1;
                await updateCartItemUI(prod.id, currentQty + 1, cartProductContainer);
            });

            btnTrash.addEventListener("click", async (): Promise<void> => {
                if (confirm(`¿Eliminar ${prod.name} del carrito?`)) {
                    await updateCartItemUI(prod.id, 0, cartProductContainer);
                }
            });
        });
        cartProductContainer.appendChild(fragment);
    } else {
        renderEmptyCartUI(cartProductContainer);
    }
    // Actualizamos el resumen del carrito
    updateCartSummary(cartItems);

    // Actualizamos el badge del carrito
    updateCartBadge();
};

/**
 * Renderiza el bloque visual correspondiente a un carrito sin ítems
 * y agrega el evento para volver a la tienda al botón correspondiente.
 * @param container El elemento HTML donde se renderizará el mensaje de carrito vacío
 */
const renderEmptyCartUI = (container: HTMLElement): void => {
    const cartEmpty = document.createElement("div");
    const emptyResult = document.createElement("p");
    const btnReturnStore = document.createElement("button");

    cartEmpty.classList.add("cart__empty");
    emptyResult.classList.add("empty-result");
    emptyResult.textContent = "No se encontraron productos en el carrito.";
    btnReturnStore.classList.add("btn", "btn--primary");
    btnReturnStore.textContent = "Volver a la tienda";

    cartEmpty.appendChild(emptyResult);
    cartEmpty.appendChild(btnReturnStore);
    container.appendChild(cartEmpty);

    btnReturnStore.addEventListener("click", (): void => {
        navigate(PATHS.STORE.HOME);
    });
};

/**
 * Función para actualizar la interfaz de un producto específico en el carrito
 * cuando se cambia su cantidad o se elimina sin necesidad de recargar toda la vista.
 * Desenuelve correctamente la Promise<boolean> de validación de stock.
 * @param id ID del producto a actualizar
 * @param newQty Nueva cantidad del producto (si es 0 o menor, se eliminará del carrito)
 * del carrito después de la actualización
 * @param cartContainer El elemento HTML que contiene los productos del carrito
 */
export const updateCartItemUI = async (id: string, newQty: number, cartContainer: HTMLElement): Promise<void> => {
    // 1. Buscamos la "fila" específica por su dataset id
    const itemContainer = cartContainer.querySelector<HTMLElement>(`[data-id="${id}"]`);
    if (!itemContainer) return;

    if (newQty <= 0) {
        // Eliminar del DOM y storage
        storage.removeCartItem(id);
        itemContainer.remove();

        // Verificamos si el carrito quedó vacío
        const cartItems: ICartItem[] = storage.getCartItems();
        if (cartItems.length === 0) {
            await showCart();

            return; // Cortamos el flujo, el propio showCart limpiará todo el DOM
        }
    } else {
        const input = itemContainer.querySelector<HTMLInputElement>(".product-qty");

        // Actualizamos storage y verificamos stock
        const success: boolean = await storage.updateCartItem(id, newQty);
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
        const subtotalElement = itemContainer.querySelector<HTMLParagraphElement>(".cart__product-price--subtotal");

        if (subtotalElement) {
            const nuevoSubtotal: number = price * newQty;
            subtotalElement.innerHTML = formattedPriceHTML(nuevoSubtotal);
        }
    }

    // 4. Actualizamos el resumen total de forma dinámica
    const activeProducts: IProduct[] = await productService.getAll();
    const updatedCartItems: IProduct[] = cartService.getCartItems(activeProducts);
    updateCartSummary(updatedCartItems);

    // Y actualizamos el badge de la barra de navegación superior
    updateCartBadge();
};

/**
 * Calcula el subtotal del carrito
 * @param items lista de productos en el carrito
 * @returns el monto subtotal
 */
const getSubtotal = (items: IProduct[]): number => {
    const cartData: ICartItem[] = storage.getCartItems();
    return items.reduce((acc: number, prod: IProduct): number => {
        const qty: number = cartData.find((i: ICartItem): boolean => i.id === prod.id)?.qty || 0;

        return acc + prod.price * qty;
    }, 0);
};

/**
 * Actualiza un elemento del DOM con un monto formateado
 * @param selector selector del elemento DOM
 * @param amount monto a renderizar
 * @param container contenedor donde buscar el elemento (por defecto, document)
 */
const renderAmount = (selector: string, amount: number, container: ParentNode = document): void => {
    const element = container.querySelector<HTMLElement>(selector);
    if (element) {
        element.innerHTML = formattedPriceHTML(amount);
    }
};

/**
 * Función para actualizar el resumen del carrito (subtotal, envío, total)
 * @param items lista de productos actualmente en el carrito
 */
const updateCartSummary = (items: IProduct[]): void => {
    const subtotal: number = getSubtotal(items);
    const shippingCost: number = subtotal > 0 ? 500 : 0;

    renderAmount("#cart-subtotal-amount", subtotal);
    renderAmount("#cart-shipping-amount", shippingCost);
    renderAmount("#cart-total-amount", subtotal + shippingCost);
};

/**
 * Función para inicializar los eventos del carrito
 */
export const initCartEvents = (): void => {
    const btnClearCart = document.querySelector<HTMLButtonElement>("#btn-clear-cart");
    const btnCheckout = document.querySelector<HTMLButtonElement>("#btn-checkout");

    // Evento del botón "Vaciar carrito"
    if (btnClearCart) {
        btnClearCart.addEventListener("click", async (): Promise<void> => {
            // Ahora el confirm solo se disparará una vez
            if (confirm("¿Desea vaciar el carrito?")) {
                storage.clearCart();
                // Refrescamos la vista pasando la lista de productos
                await showCart();
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
