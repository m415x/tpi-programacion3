import type { Product } from "@interfaces/Product";
import { cartService as cs } from "@services/cartService";
import { updateCartBadge } from "@utils/components";
import { navigate } from "@utils/navigate";
import { PATHS } from "@utils/paths";
import { storage } from "@utils/storage";
import {
    updateProductImageUI,
    formattedPriceHTML,
    getDisabledState,
} from "@utils/uiUtils";

/**
 * Muestra el detalle de un producto específico, incluyendo su imagen, descripción,
 * precio y opciones para agregar al carrito.
 * @param product El producto del cual se desea mostrar el detalle.
 */
export const showProductDetail = (product: Product): void => {
    const productDetailContainer = document.querySelector<HTMLElement>(
        "#product-detail-container",
    );
    const pageTitle = document.querySelector<HTMLElement>("title");

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!productDetailContainer || !pageTitle) return;

    // Actualizar el título de la página
    pageTitle.innerText = "Food Store - " + product.nombre;

    // Formatear el precio
    const unitPrice: string = formattedPriceHTML(product.precio);

    // Obtener la cantidad actual desde el storage para este producto
    const qtyInCart: number = cs.getProductQuantity(product.id);

    // Calcular el stock disponible para mostrar
    const displayStock: number = product.stock - qtyInCart;

    // Determinar si habilitar elemento
    const isActuallyAvailable: boolean = product.disponible && displayStock > 0;
    const disabledState: string = getDisabledState(isActuallyAvailable);
    const disabledStateBadge: string = getDisabledState(
        isActuallyAvailable,
        "stock-badge--out-of-stock",
    );

    // Estado inicial: Iniciamos en 1 si hay stock
    let selectedQty: number = isActuallyAvailable ? 1 : 0;

    // Actualizar el badge
    const badgeText: string = isActuallyAvailable
        ? `Disponible (Stock ${displayStock})`
        : "Agotado";
    const btnTextAddToCart: string = isActuallyAvailable
        ? `Agregar al carrito`
        : `No disponible`;

    if (productDetailContainer) {
        productDetailContainer.innerHTML = `
            <div class="card product-detail__img-container">
                <img class="product-detail__img" src="" id="img-product-${product.id}" alt="${product.nombre}">
            </div>
            <div class="product-detail__details">
                <h4 class="product-detail__title">${product.nombre}</h4>
                <p class="price product-detail__price">${unitPrice}</p>
                <p class="stock-badge ${disabledStateBadge}">${badgeText}</p>
                <p class="product-detail__description">${product.descripcion}</p>
                <p class="product-detail__subtitle">Cantidad:</p>
                <div class="product-detail__row">
                    <button class="btn btn--square btn--minus" ${disabledState}>-</button>
                    <input type="number" id="product-detail-qty" class="product-qty" value="${selectedQty}" min="1" max="${displayStock}" ${disabledState}>
                    <button class="btn btn--square btn--plus" ${disabledState}>+</button>
                </div>
                <div class="product-detail__row">
                    <button class="btn btn--primary btn--add-product" ${disabledState}>${btnTextAddToCart}</button>
                    <button class="btn btn--secondary">Volver</button>
                </div>
            </div>
            `;

        // Iniciamos la carga asíncrona de la imagen
        updateProductImageUI(product.id);

        const inputQty =
            productDetailContainer.querySelector<HTMLInputElement>(
                ".product-qty",
            );
        const btnAdd =
            productDetailContainer.querySelector<HTMLButtonElement>(
                ".btn--add-product",
            );
        const btnPlus =
            productDetailContainer.querySelector<HTMLButtonElement>(
                ".btn--plus",
            );
        const btnMinus =
            productDetailContainer.querySelector<HTMLButtonElement>(
                ".btn--minus",
            );

        // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
        if (!inputQty || !btnAdd || !btnPlus || !btnMinus) return;

        // Función interna para actualizar el número visual sin tocar el Storage
        const updateLocalUI = (): void => {
            if (selectedQty > displayStock) selectedQty = displayStock;
            if (selectedQty < 1) selectedQty = 1;
            inputQty.value = selectedQty.toString();
        };

        // Eventos de cantidad (Modifican solo la variable local)
        btnPlus.addEventListener("click", (): void => {
            if (selectedQty < displayStock) {
                selectedQty++;
                updateLocalUI();
            }
        });

        btnMinus.addEventListener("click", (): void => {
            if (selectedQty > 1) {
                selectedQty--;
                updateLocalUI();
            }
        });

        // Evento de input
        inputQty.addEventListener("change", (): void => {
            const val: number = parseInt(inputQty.value, 10);
            selectedQty = isNaN(val) ? 1 : val;
            updateLocalUI();
        });

        // Evento de agregar al carrito (Intenta actualizar el Storage)
        btnAdd.addEventListener("click", (): void => {
            // Al sumar la cantidad seleccionada a lo que ya había en el carrito
            const totalNewQty: number = qtyInCart + selectedQty;

            if (storage.updateCartItem(product.id, totalNewQty)) {
                alert(
                    `Agregaste ${selectedQty} unidad(es) de ${product.nombre}`,
                );
                showProductDetail(product); // Refrescamos para actualizar el stock visual remanente
            } else {
                alert(
                    "Error: La cantidad seleccionada supera el stock disponible.",
                );
            }
        });

        // Evento de volver
        const btnBack = productDetailContainer.querySelector<HTMLButtonElement>(
            ".product-detail__row .btn--secondary",
        );

        // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
        if (!btnBack) return;

        btnBack.addEventListener("click", (): void => {
            navigate(PATHS.STORE.HOME);
        });
    }

    // Actualizamos el badge del carrito
    updateCartBadge();
};
