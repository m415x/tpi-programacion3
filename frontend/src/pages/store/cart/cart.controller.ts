import type { ICartItem } from "@interfaces/ICartItem";
import type { IProduct } from "@interfaces/Product.interface";
import { OrderStatus, PaymentMethod } from "@interfaces/Enums";
import { cartService } from "@services/cart.service";
import { orderService } from "@services/order.service";
import { productService } from "@services/product.service";
import { updateCartBadge } from "@utils/components";
import { navigate } from "@utils/navigate";
import { PATHS } from "@utils/paths";
import { storage } from "@utils/storage";
import { formattedPriceHTML, wrapWithDetailLink } from "@utils/uiUtils";

// Estado local del acumulador para transferir el monto exacto al modal popup
let currentTotalAmount: number = 0;

// Selector diferido para capturar los nodos del Modal Checkout tras la inyección
const getCheckoutElements = () => ({
    modalOverlay: document.querySelector<HTMLDivElement>("#checkout-modal-overlay")!,
    checkoutForm: document.querySelector<HTMLFormElement>("#checkout-form")!,
    btnCloseModal: document.querySelector<HTMLButtonElement>("#btn-close-checkout")!,
    modalTotalLabel: document.querySelector<HTMLElement>("#modal-total-amount")!,
    inputPhone: document.querySelector<HTMLInputElement>("#checkout-phone")!,
    inputAddress: document.querySelector<HTMLTextAreaElement>("#checkout-address")!,
    selectPayment: document.querySelector<HTMLSelectElement>("#checkout-payment")!,
    inputNotes: document.querySelector<HTMLTextAreaElement>("#checkout-notes")!,
});

/**
 * Función para cargar los productos del carrito en el contenedor principal
 */
export const showCart = async (): Promise<void> => {
    const cartProductContainer = document.querySelector<HTMLElement>("#cart-product-container");
    if (!cartProductContainer) return;

    // Aseguramos que la estructura del Modal Popup de Checkout esté presente en el DOM
    ensureCheckoutModalDOM();

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
 * Inyecta el esqueleto HTML del popup de checkout si no existe en el documento actual.
 */
const ensureCheckoutModalDOM = (): void => {
    if (document.querySelector("#checkout-modal-overlay")) return;

    const modalDiv = document.createElement("div");
    modalDiv.id = "checkout-modal-overlay";
    modalDiv.className = "admin-modal-overlay hidden"; // Reutiliza tus clases de layout de modales
    modalDiv.innerHTML = `
        <div class="form-container admin-modal-content">
            <div class="admin-header-row checkout-header-margin">
                <h2>Completar Pedido</h2>
                <button type="button" id="btn-close-checkout" class="btn-close-modal-x">×</button>
            </div>
            <form id="checkout-form" novalidate>
                <div class="form-group">
                    <label for="checkout-phone">Teléfono</label>
                    <input type="text" id="checkout-phone" required placeholder="Ej: +54 9 264 123-4567">
                </div>

                <div class="form-group">
                    <label for="checkout-address">Dirección de Entrega</label>
                    <textarea id="checkout-address" required placeholder="Calle, número, piso, depto..."></textarea>
                </div>

                <div class="form-group">
                    <label for="checkout-payment">Método de Pago</label>
                    <select id="checkout-payment" required>
                        <option value="">Seleccione un método</option>
                        <option value="${PaymentMethod.CASH}">Efectivo</option>
                        <option value="${PaymentMethod.CARD}">Tarjeta</option>
                        <option value="${PaymentMethod.TRANSFER}">Transferencia Bancaria</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="checkout-notes">Notas adicionales (opcional)</label>
                    <textarea id="checkout-notes" placeholder="Instrucciones especiales, timbre, etc."></textarea>
                </div>

                <div class="admin-header-row checkout-total-row">
                    <h3>Total a pagar:</h3>
                    <h3 id="modal-total-amount" class="price">$-</h3>
                </div>

                <button type="submit" class="btn btn--tertiary btn--full btn-checkout-submit">Confirmar Pedido</button>
            </form>
        </div>
    `;
    document.body.appendChild(modalDiv);
    initCheckoutModalEvents();
};

/**
 * Inicializa los eventos del Modal Popup de Finalización de Compra.
 */
const initCheckoutModalEvents = (): void => {
    const checkout = getCheckoutElements();

    // Evento de Cierre (Botón X)
    checkout.btnCloseModal.addEventListener("click", () => {
        checkout.modalOverlay.classList.add("hidden");
    });

    // Cierre defensivo por click exterior
    checkout.modalOverlay.addEventListener("click", (e) => {
        if (e.target === checkout.modalOverlay) {
            checkout.modalOverlay.classList.add("hidden");
        }
    });

    // Envío del Formulario (Submit) contra Spring Boot
    // Envío del Formulario (Submit) contra Spring Boot en DOS PASOS
    checkout.checkoutForm.addEventListener("submit", async (e: Event): Promise<void> => {
        e.preventDefault();

        if (!checkout.inputPhone.value.trim() || !checkout.inputAddress.value.trim() || !checkout.selectPayment.value) {
            alert("Por favor, completá todos los campos obligatorios del envío.");
            return;
        }

        try {
            // Obtenemos los ítems crudos del storage de sesión
            const rawItems: ICartItem[] = storage.getCartItems();
            if (rawItems.length === 0) {
                alert("El carrito se encuentra vacío.");
                return;
            }

            // 🚀 PASO 1: Creamos el cascarón de la orden en el Backend (Sin orderDetails)
            const createdOrder = await orderService.create({
                date: new Date().toISOString().split("T")[0] || "",
                orderStatus: OrderStatus.PENDING,
                total: currentTotalAmount, // Java lo ignora y lo pisa con ZERO, pero cumple el contrato
                paymentMethod: checkout.selectPayment.value as PaymentMethod,
                userId: storage.getUser()?.id || "",
                customerPhone: checkout.inputPhone.value.trim(),
                shippingAddress: checkout.inputAddress.value.trim(),
                customerNotes: checkout.inputNotes.value.trim() || "Sin observaciones adicionales.",
            });

            // 🚀 PASO 2: Iteramos el carrito y agregamos los ítems uno por uno al sub-recurso
            for (const item of rawItems) {
                await orderService.addItemToOrder(createdOrder.id, item.id, item.qty);
            }

            alert("¡Pedido generado exitosamente! Podrás seguir su estado desde 'Mis Pedidos'.");

            // Limpieza del ecosistema local tras la compra
            storage.clearCart();
            checkout.checkoutForm.reset();
            checkout.modalOverlay.classList.add("hidden");

            // Redirigimos al cliente a su historial
            navigate(PATHS.CLIENT.ORDERS);
        } catch (error: any) {
            console.error("Error al procesar el checkout:", error);
            alert(error.response?.data?.message || "Error perimetral de red al procesar tu orden.");
        }
    });
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
        btnCheckout.addEventListener("click", async (): Promise<void> => {
            // 1. Buscamos los ítems actuales en el storage para validar que no esté vacío
            const cartItems = storage.getCartItems();
            if (cartItems.length === 0) {
                alert("Agregá productos al carrito antes de proceder.");
                return;
            }

            // 2. Recalculamos el total real en caliente para asegurar sincronización absoluta
            const activeProducts = await productService.getAll();
            const productsInCart = cartService.getCartItems(activeProducts);

            const subtotal = getSubtotal(productsInCart);
            const shippingCost = subtotal > 0 ? 500 : 0;

            // Actualizamos la variable global para el submit del formulario
            currentTotalAmount = subtotal + shippingCost;

            // 3. Capturamos los elementos del Modal Checkout usando los selectores dinámicos
            const checkout = getCheckoutElements();

            // 3. Inyectamos el total calculado al centavo en el tag del popup
            checkout.modalTotalLabel.innerHTML = formattedPriceHTML(currentTotalAmount);

            // 4. Removemos la clase utilitaria que creamos en el CSS para hacerlo emerger
            checkout.modalOverlay.classList.remove("hidden");
        });
    }
};
