import type { ICartItem } from "@interfaces/ICartItem";
import type { Product } from "@interfaces/Product";
import { cartService as cs } from "@services/cartService";
import { productService as ps } from "@services/productService";
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
    const activeProducts: Product[] = ps.getActiveProducts(products);

    // Filtramos los productos del carrito
    const cartItems: Product[] = cs.getCartItems(activeProducts);

    // Limpiar el contenedor de productos
    cartProductContainer ? (cartProductContainer.innerHTML = "") : null;

    // Si hay productos en el carrito, los mostramos. De lo contrario,
    // mostramos mensaje de carrito vacío.
    if (cartItems.length > 0) {
        cartItems.forEach((prod: Product): void => {
            const article: HTMLElement = document.createElement("article");
            article.classList.add("card", "cart__product");

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
            const currentQty: number = cs.getProductQuantity(prod.id);
            const inputQty =
                article.querySelector<HTMLInputElement>(".product-qty");

            // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
            if (!inputQty) return;

            // Establecer el máximo en base al stock disponible
            inputQty.value = currentQty.toString();
            inputQty.addEventListener("change", (): void => {
                const newQty: number = parseInt(inputQty.value, 10);
                if (newQty > 0) {
                    // Actualizamos el storage
                    handleUpdateQuantity(products, prod.id, newQty);
                }
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
                storage.decreaseCartItem(prod.id);
                showCart(products);
            });
            btnPlus.addEventListener("click", (e: Event): void => {
                e.preventDefault();

                // Actualizamos el storage
                handleUpdateQuantity(products, prod.id);
            });
            btnTrash.addEventListener("click", (): void => {
                if (confirm(`¿Eliminar ${prod.nombre} del carrito?`)) {
                    storage.removeCartItem(prod.id);
                    showCart(products);
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
 * Función para actualizar el resumen del carrito (subtotal, envío, total)
 * @param items lista de productos actualmente en el carrito
 */
const updateCartSummary = (items: Product[]) => {
    const subtotalElement = document.querySelector<HTMLParagraphElement>(
        "#cart-subtotal-amount",
    );
    const shippingCosts = document.querySelector<HTMLParagraphElement>(
        "#cart-shipping-amount",
    );
    const totalElement =
        document.querySelector<HTMLParagraphElement>("#cart-total-amount");

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!subtotalElement || !shippingCosts || !totalElement) return;

    const cartData: ICartItem[] = storage.getCartItems();

    // Calculamos el subtotal
    const subtotal: number = items.reduce(
        (acc: number, prod: Product): number => {
            const qty: number =
                cartData.find((i: ICartItem): boolean => i.id === prod.id)
                    ?.qty || 0;
            return acc + prod.precio * qty;
        },
        0,
    );

    // Actualizamos el subtotal
    if (subtotalElement) {
        const totalPrice: string = formattedPriceHTML(subtotal);
        subtotalElement.innerHTML = totalPrice;
    }

    // Calculamos el costo de envío
    const shippingCost: number = subtotal > 0 ? 500 : 0;
    if (shippingCosts) {
        const shippingPrice: string = formattedPriceHTML(shippingCost);
        shippingCosts.innerHTML = shippingPrice;
    }

    // Calculamos el total
    const total: number = subtotal + shippingCost;
    if (totalElement) {
        const totalPrice: string = formattedPriceHTML(total);
        totalElement.innerHTML = totalPrice;
    }
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

/**
 * Función para manejar la actualización de cantidad desde el input numérico
 * @param id ID del producto a actualizar
 * @param targetQty Cantidad objetivo a establecer para el producto en el carrito
 */
export const handleUpdateQuantity = (
    products: Product[],
    id: number,
    targetQty?: number,
): void => {
    // Actualizamos el storage
    const success: boolean = storage.updateCartItem(id, targetQty);
    if (!success) {
        alert("No hay suficiente stock");
    }

    // Refrescamos la vista para actualizar subtotales y totales
    showCart(products);
};
