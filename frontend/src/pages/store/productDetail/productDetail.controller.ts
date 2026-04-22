import type { Product } from "@interfaces/Product";
import { storage } from "@utils/storage";
import { cartService as cs } from "@/services/cartService";
import { updateProductImageUI, formattedPriceHTML } from "@/utils/uiUtils";
import { updateCartBadge } from "@/utils/components";
import { navigate } from "@/utils/navigate";
import { PATHS } from "@utils/paths";

// Función para cargar los productos en el contenedor principal
export const showProductDetail = (product: Product): void => {
    const productDetailContainer = document.querySelector(
        "#product-detail-container",
    ) as HTMLElement;

    const unitPrice: string = formattedPriceHTML(product.precio);

    // Obtener la cantidad actual desde el storage para este producto
    const currentQty: number = cs.getProductQuantity(product.id);

    // Calcular el stock disponible para mostrar
    const displayStock: number = product.stock - currentQty;

    // Determinar si habilitar el botón
    const isActuallyAvailable: boolean = product.disponible && displayStock > 0;

    const btnAvailable: string = isActuallyAvailable
        ? `<button class="btn btn--primary btn--add-product">Agregar al carrito</button>`
        : `<button class="btn btn--secondary btn--add-product btn--disabled" disabled >No disponible</button>`;

    if (productDetailContainer) {
        productDetailContainer.innerHTML = `
            <div class="card product-detail__img-container">
                <img class="product-detail__img" src="" id="img-product-${product.id}" alt="${product.nombre}">
            </div>
            <div class="product-detail__details">
                <h4 class="product-detail__title">${product.nombre}</h4>
                <p class="price product-detail__price">${unitPrice}</p>
                <p class="stock-badge ${isActuallyAvailable ? "stock-badge--available" : ""}">${isActuallyAvailable ? `Disponible (Stock ${displayStock})` : "Agotado"}</p>
                <p class="product-detail__description">${product.descripcion}</p>
                <p class="product-detail__subtitle">Cantidad</p>
                <div class="product-detail__row">
                    <button class="btn btn--square btn--minus">-</button>
                    <input type="number" class="product-qty" value="" min="1">
                    <button class="btn btn--square btn--plus">+</button>
                </div>
                <div class="product-detail__row">
                    ${btnAvailable}
                    <button class="btn btn--secondary">Volver</button>
                </div>
            </div>
            `;

        // Iniciamos la carga asíncrona de la imagen
        updateProductImageUI(product.id);

        const inputQty = productDetailContainer.querySelector(
            ".product-qty",
        ) as HTMLInputElement;
        inputQty.value = currentQty.toString();
        inputQty.addEventListener("change", (): void => {
            const newQty: number = parseInt(inputQty.value, 10);
            if (newQty > 0) {
                const success: boolean = storage.updateCartItem(
                    product.id,
                    newQty,
                );
                if (!success) {
                    alert("No hay suficiente stock");
                }
                showProductDetail(product);
            }
        });

        // Eventos de botones
        const btnMinus = productDetailContainer.querySelector(
            ".btn--minus",
        ) as HTMLButtonElement;
        btnMinus.addEventListener("click", (): void => {
            storage.decreaseCartItem(product.id);
            showProductDetail(product);
        });

        const btnPlus = productDetailContainer.querySelector(
            ".btn--plus",
        ) as HTMLButtonElement;
        btnPlus.addEventListener("click", (e: Event): void => {
            e.preventDefault();
            const success: boolean = storage.updateCartItem(product.id);

            if (success) {
                showProductDetail(product);
            } else {
                alert("No hay más stock disponible");
            }
        });

        const btnBack = productDetailContainer.querySelector(
            ".product-detail__row .btn--secondary",
        ) as HTMLButtonElement;

        btnBack.addEventListener("click", (): void => {
            navigate(PATHS.STORE.HOME);
        });
    }

    // Actualizamos el badge del carrito
    updateCartBadge();
};
