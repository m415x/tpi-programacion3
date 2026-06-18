import type { IProduct } from "@interfaces/Product.interface";
import { cartService } from "@services/cart.service";
import { productService } from "@services/product.service";
import { updateCartBadge } from "@utils/components";
import { navigate } from "@utils/navigate";
import { PATHS } from "@utils/paths";
import { storage } from "@utils/storage";
import {
    formattedPriceHTML,
    getItemAvailability,
    setPageTitle,
    showCartNotice,
    updateBaseAvailabilityUI,
} from "@utils/uiUtils";

/**
 * Obtiene un producto específico desde la base de datos de Spring Boot por su ID,
 * e inyecta toda su información y controles reactivos en el DOM.
 * @param productId ID del producto a consultar en el servidor
 */
export const showProductDetail = async (productId: number): Promise<void> => {
    const productDetailContainer = document.querySelector<HTMLElement>("#product-detail-container");
    if (!productDetailContainer) return;

    // 1. Buscamos el producto fresco en tiempo real al backend
    const product: IProduct = await productService.getById(productId);

    // Actualizamos el título de la página
    setPageTitle(product.name);

    // Formatear el precio
    const unitPrice: string = formattedPriceHTML(product.price);

    // Calcular disponibilidad
    const { available, isAvailable } = getItemAvailability(product);

    // Estado inicial: Iniciamos en 1 si hay stock
    let selectedQty: number = isAvailable ? 1 : 0;

    productDetailContainer.innerHTML = `
        <div class="card product-detail__img-container">
            <img class="product-detail__img" src="${product.imageUrl}" id="img-product-${product.id}" alt="${product.name}">
        </div>
        <div class="product-detail__details">
            <h4 class="product-detail__title">${product.name}</h4>
            <p class="price product-detail__price">${unitPrice}</p>
            <p class="stock-badge"></p> <!-- Lo renderiza renderStockStatus -->
            <p class="product-detail__description">${product.description}</p>
            <p class="product-detail__subtitle">Cantidad:</p>
            <div class="product-detail__row">
                <button class="btn btn--square btn--minus">-</button>
                <input type="number" id="product-detail-qty" class="product-qty" value="${selectedQty}" min="1">
                <button class="btn btn--square btn--plus">+</button>
            </div>
            <div class="product-detail__row">
                <button class="btn btn--primary btn--add-product"></button>
                <button class="btn btn--secondary">Volver</button>
            </div>
        </div>
    `;

    // Renderizamos el estado de stock y disponibilidad
    renderStockStatus(productDetailContainer, available, isAvailable);

    const inputQty = productDetailContainer.querySelector<HTMLInputElement>(".product-qty");
    const btnAdd = productDetailContainer.querySelector<HTMLButtonElement>(".btn--add-product");
    const btnPlus = productDetailContainer.querySelector<HTMLButtonElement>(".btn--plus");
    const btnMinus = productDetailContainer.querySelector<HTMLButtonElement>(".btn--minus");
    if (!inputQty || !btnAdd || !btnPlus || !btnMinus) return;

    // Función interna para actualizar el número visual sin tocar el Storage
    const updateLocalUI = (): void => {
        if (selectedQty > available) selectedQty = available;
        if (selectedQty < 1) selectedQty = 1;
        inputQty.value = selectedQty.toString();
    };

    // Eventos de cantidad (Modifican solo la variable local)
    btnPlus.addEventListener("click", (): void => {
        if (selectedQty < available) {
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
    btnAdd.addEventListener("click", async (): Promise<void> => {
        // Calculamos la nueva cantidad total que se desea tener en el carrito para este producto
        const currentQtyInCart: number = cartService.getProductQuantity(product.id);
        const totalNewQty: number = currentQtyInCart + selectedQty;

        const wasUpdated: boolean = await storage.updateCartItem(product.id, totalNewQty);

        if (wasUpdated) {
            // Recalculamos disponibilidad actualizada
            const updated = getItemAvailability(product);

            // Actualizamos la UI de stock de forma atómica
            renderStockStatus(productDetailContainer, updated.available, updated.isAvailable);

            const detailsContainer = productDetailContainer.querySelector<HTMLElement>(".product-detail__details");

            // Mostramos el aviso de agregado al carrito
            if (detailsContainer) {
                showCartNotice(detailsContainer, selectedQty, product.name, "append");
            }

            selectedQty = updated.isAvailable ? 1 : 0;
            updateLocalUI();

            // Actualizamos el badge del carrito en el navbar
            updateCartBadge();
        } else {
            alert("La cantidad seleccionada supera el stock disponible.");
        }
    });

    // Evento de volver
    const btnBack = productDetailContainer.querySelector<HTMLButtonElement>(".product-detail__row .btn--secondary");
    if (!btnBack) return;

    btnBack.addEventListener("click", (): void => {
        navigate(PATHS.STORE.HOME);
    });

    // Actualizamos el badge del carrito
    updateCartBadge();
};

/**
 * Actualiza la interfaz de usuario para reflejar el estado de stock y disponibilidad
 * de un producto. Modifica el badge de stock, el botón de agregar al carrito y los
 * controles de cantidad según corresponda.
 * @param container El contenedor HTML donde se encuentran los elementos a actualizar.
 * @param available La cantidad disponible restante del producto.
 * @param isAvailable Un booleano que indica si el producto está disponible para agregar al carrito.
 */
const renderStockStatus = (container: HTMLElement, available: number, isAvailable: boolean): void => {
    // Actualizamos el badge de stock y el botón de agregar al carrito
    updateBaseAvailabilityUI(container, isAvailable, {
        badge: ".stock-badge",
        button: ".btn--add-product",
    });

    const badge = container.querySelector<HTMLElement>(".stock-badge");
    if (badge) {
        // Actualizamos el texto y la clase del badge de stock
        badge.textContent = isAvailable ? `Disponible (Stock ${available})` : "Agotado";
    }

    const inputQty = container.querySelector<HTMLInputElement>(".product-qty");
    const btnPlus = container.querySelector<HTMLButtonElement>(".btn--plus");
    const btnMinus = container.querySelector<HTMLButtonElement>(".btn--minus");
    if (!inputQty || !btnPlus || !btnMinus) return;

    // Deshabilita los controles de cantidad si no hay stock
    [inputQty, btnPlus, btnMinus].forEach((el: HTMLElement | null): void => {
        if (el instanceof HTMLButtonElement || el instanceof HTMLInputElement) {
            el.disabled = !isAvailable;
        }
    });

    if (inputQty) {
        // Ajustamos el valor máximo del input a la cantidad disponible
        inputQty.max = available.toString();
        if (!isAvailable) inputQty.value = "0";
    }
};
