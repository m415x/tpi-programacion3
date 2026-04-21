import type { Product } from "@interfaces/Product";
import { storage } from "@utils/storage";
import { productService as ps } from "@/services/productService";
import { cartService as cs } from "@/services/cartService";
import { updateProductImageUI, formattedPriceHTML } from "@/utils/uiUtils";
import type { ICartItem } from "@/types/ICartItem";
import { updateCartBadge } from "@/utils/components";
import { navigate } from "@/utils/navigate";
import { PATHS } from "@utils/paths";

// Función para cargar los productos en el contenedor principal
export const showCart = (products: Product[]): void => {
    const cartProductContainer = document.querySelector<HTMLElement>(
        "#cart-product-container",
    );

    const activeProducts: Product[] = ps.getActiveProducts(products);

    const cartItems: Product[] = cs.getCartItems(activeProducts);

    // Limpiar el contenedor de productos
    cartProductContainer ? (cartProductContainer.innerHTML = "") : null;

    if (cartItems.length > 0) {
        cartItems.forEach((prod: Product): void => {
            const article: HTMLElement = document.createElement("article");
            article.classList.add("card", "cart__product");

            const unitPrice: string = formattedPriceHTML(prod.precio);

            article.innerHTML = `
            <div class="cart__product-info">
                <img class="cart__product-img" src="" id="img-product-${prod.id}" alt="${prod.nombre}">
                <div class="cart__product-details">
                    <h4 class="cart__product-title">${prod.nombre}</h4>
                    <p class="cart__product-description">${prod.descripcion}</p>
                    <p class="price cart__product-price">${unitPrice}<span>c/u</span></p>
                </div>
              </div>
              <div class="cart__product-btns">
                <button class="btn btn--product-cart btn--minus">-</button>
                <input type="number" class="product-qty" value="" min="1">
                <button class="btn btn--product-cart btn--plus">+</button>
                <p class="price cart__product-price cart__product-price--subtotal"></p>
                <button class="btn btn--product-cart btn--trash">🗑</button>
              </div>
            `;
            cartProductContainer?.appendChild(article);

            // Iniciamos la carga asíncrona de la imagen
            updateProductImageUI(prod.id);

            // Obtener la cantidad actual desde el storage para este producto
            const currentQty: number =
                storage
                    .getCartItems()
                    .find((i: ICartItem): boolean => i.id === prod.id)?.qty ||
                1;
            const inputQty = article.querySelector(
                ".product-qty",
            ) as HTMLInputElement;
            inputQty.value = currentQty.toString();
            inputQty.addEventListener("change", (): void => {
                const newQty: number = parseInt(inputQty.value, 10);
                if (newQty > 0) {
                    const success = storage.updateCartItem(prod.id, newQty);
                    if (!success) {
                        alert("No hay suficiente stock");
                    }
                    showCart(products);
                }
            });

            // Calcular subtotal de este producto
            const subtotalValue: number = prod.precio * currentQty;
            const subtotalPrice: string = formattedPriceHTML(subtotalValue);
            const subtotalElement = article.querySelector(
                ".cart__product-price--subtotal",
            ) as HTMLParagraphElement;
            subtotalElement.innerHTML = subtotalPrice;

            // Eventos de botones

            const btnMinus = article.querySelector(
                ".btn--minus",
            ) as HTMLButtonElement;
            btnMinus.addEventListener("click", (): void => {
                storage.decreaseCartItem(prod.id);
                showCart(products);
            });

            const btnPlus = article.querySelector(
                ".btn--plus",
            ) as HTMLButtonElement;
            btnPlus.addEventListener("click", (e: Event): void => {
                e.preventDefault();
                const success = storage.updateCartItem(prod.id);

                if (success) {
                    showCart(products);
                } else {
                    alert("No hay más stock disponible");
                }
            });

            const btnTrash = article.querySelector(
                ".btn--trash",
            ) as HTMLButtonElement;
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

const updateCartSummary = (items: Product[]) => {
    const subtotalElement = document.querySelector(
        "#cart-subtotal-amount",
    ) as HTMLParagraphElement;

    const shippingCosts = document.querySelector(
        "#cart-shipping-amount",
    ) as HTMLParagraphElement;

    const totalElement = document.querySelector(
        "#cart-total-amount",
    ) as HTMLParagraphElement;

    const cartData: ICartItem[] = storage.getCartItems();

    const subtotal: number = items.reduce(
        (acc: number, prod: Product): number => {
            const qty: number =
                cartData.find((i: ICartItem): boolean => i.id === prod.id)
                    ?.qty || 0;
            return acc + prod.precio * qty;
        },
        0,
    );

    if (subtotalElement) {
        const totalPrice: string = formattedPriceHTML(subtotal);
        subtotalElement.innerHTML = totalPrice;
    }

    const shippingCost: number = subtotal > 0 ? 500 : 0;
    if (shippingCosts) {
        const shippingPrice: string = formattedPriceHTML(shippingCost);
        shippingCosts.innerHTML = shippingPrice;
    }

    const total: number = subtotal + shippingCost;
    if (totalElement) {
        const totalPrice: string = formattedPriceHTML(total);
        totalElement.innerHTML = totalPrice;
    }

    const btnClearCart = document.querySelector(
        "#btn-clear-cart",
    ) as HTMLButtonElement;

    if (btnClearCart) {
        btnClearCart.addEventListener("click", (): void => {
            if (confirm("¿Desea vaciar el carrito?")) {
                storage.clearCart();
                showCart(items);
            }
        });
    }
};
