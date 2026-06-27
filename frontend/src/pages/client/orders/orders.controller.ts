import { orderService } from "@services/order.service";
import { storage } from "@utils/storage";
import {
    formattedPriceHTML,
    translatePaymentMethod,
    translateStatusOrder,
    getOrderStatusMessage,
} from "@utils/uiUtils";
import type { IOrder } from "@interfaces/Order.interface";
import type { IOrderDetail } from "@interfaces/OrderDetail.interface";

// Selectores del DOM encapsulados
const dom = {
    get tableBody() {
        return document.querySelector<HTMLTableSectionElement>("#client-orders-table-body")!;
    },
    get modalOverlay() {
        return document.querySelector<HTMLDivElement>("#order-items-modal-overlay")!;
    },
    get modalOrderId() {
        return document.querySelector<HTMLSpanElement>("#detail-modal-order-id")!;
    },
    get modalItemsBody() {
        return document.querySelector<HTMLTableSectionElement>("#modal-order-items-body")!;
    },
    get modalTotal() {
        return document.querySelector<HTMLHeadingElement>("#detail-modal-total")!;
    },
    get btnCloseModal() {
        return document.querySelector<HTMLButtonElement>("#btn-close-items-modal")!;
    },
};

// Guardamos una copia inalterada de los pedidos del cliente para filtrar en memoria sin perder datos
let allCustomerOrdersCache: IOrder[] = [];
let customerOrders: IOrder[] = [];

export const clientOrdersController = {
    /**
     * Punto de entrada para inicializar el historial de pedidos del cliente.
     */
    init(container: HTMLElement): void {
        // Buscamos el tbody dentro del layout ya renderizado
        const tableBody = container.querySelector<HTMLTableSectionElement>("#client-orders-table-body");
        if (!tableBody) return;

        // Vinculamos cierres y escapes del modal
        this.bindModalEvents(document.body as HTMLElement);

        // NUEVO: Vinculamos el evento del dropdown de filtrado dinámico
        const filterSelect = container.querySelector<HTMLSelectElement>("#filter-order-status");
        if (filterSelect) {
            filterSelect.addEventListener("change", () => {
                const selectedStatus = filterSelect.value;

                if (selectedStatus === "ALL") {
                    customerOrders = [...allCustomerOrdersCache];
                } else {
                    customerOrders = allCustomerOrdersCache.filter((order) => order.status === selectedStatus);
                }

                // Volvemos a dibujar la tabla con el subconjunto seleccionado
                this.renderTable(tableBody, document.body as HTMLElement);
            });
        }

        // Cargamos y renderizamos las órdenes relacionales
        this.refreshOrders(tableBody, document.body as HTMLElement).catch((err) => {
            console.error("Error en el hilo de órdenes:", err);
        });
    },

    /**
     * Registra los escuchadores para controlar la apertura y cierre defensivo del modal.
     * @param container Contenedor raíz de la vista actual
     */
    bindModalEvents(container: HTMLElement): void {
        const modalOverlay = container.querySelector<HTMLDivElement>("#order-items-modal-overlay");
        const btnCloseModal = container.querySelector<HTMLButtonElement>("#btn-close-items-modal");

        if (!modalOverlay || !btnCloseModal) return;

        // Cierre por el botón '×'
        btnCloseModal.addEventListener("click", () => {
            modalOverlay.classList.add("hidden");
        });

        // Cierre defensivo por click exterior en el difuminado
        modalOverlay.addEventListener("click", (e: Event) => {
            if (e.target === modalOverlay) {
                modalOverlay.classList.add("hidden");
            }
        });
    },

    /**
     * Dibuja las filas del historial de compras en la tabla principal.
     * @param tableBody Elemento tbody de la tabla de órdenes
     * @param container Contenedor raíz para buscar el modal dinámicamente
     */
    renderTable(tableBody: HTMLTableSectionElement, container: HTMLElement): void {
        tableBody.innerHTML = "";

        if (customerOrders.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" class="text-center">Aún no has realizado ningún pedido. ¡Visitá la tienda!</td></tr>`;
            return;
        }

        customerOrders.forEach((order: IOrder) => {
            const tr = document.createElement("tr");

            // Evaluamos la clase semántica para el estado del pedido que agregamos al CSS
            const statusClass = `badge-status--${order.status.toLowerCase()}`;

            // Calculamos la cantidad total de productos sumando las cantidades de los ítems
            const totalProducts = order.orderDetails?.reduce((acc, detail) => acc + detail.quantity, 0) || 0;

            // Parseamos la fecha y hora
            const date = new Date(order.createdAt);
            const dateStr = date.toLocaleDateString();
            const timeStr = date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });

            tr.innerHTML = `
                <td><strong class="uuid-cell" title="${order.id}">#${order.id.slice(-6).toUpperCase()}</strong></td>
                <td>${dateStr} - ${timeStr} hs</td>
                <td>${totalProducts} u</td>
                <td class="price price-cell">${formattedPriceHTML(order.totalPrice)}</td>
                <td><span class="badge-status ${statusClass}">${translateStatusOrder(order.status)}</span></td>
                <td class="text-center">
                    <button class="btn btn--secondary btn-details">
                        <i class="fa-solid fa-eye"></i>
                    </button>
                </td>
            `;

            // Escuchador dinámico para abrir el desglose de productos pasando el contenedor raíz
            tr.querySelector(".btn-details")?.addEventListener("click", () => {
                this.openDetailsModal(order, container);
            });

            tableBody.appendChild(tr);
        });
    },

    /**
     * Abre el modal desglosando los ítems de la orden seleccionada de forma estricta.
     * @param order Orden de compra seleccionada
     * @param container Contenedor raíz para buscar los nodos del modal
     */
    openDetailsModal(order: IOrder, container: HTMLElement): void {
        const modalOverlay = container.querySelector<HTMLDivElement>("#order-items-modal-overlay");
        if (!modalOverlay) return;

        const modalOrderId = modalOverlay.querySelector<HTMLSpanElement>("#detail-modal-order-id");
        const modalItemsBody = modalOverlay.querySelector<HTMLTableSectionElement>("#modal-order-items-body");

        if (!modalOrderId || !modalItemsBody) return;

        modalOrderId.textContent = `#${order.id.slice(-6).toUpperCase()}`;
        modalItemsBody.innerHTML = "";

        let infoContainer = modalOverlay.querySelector<HTMLDivElement>("#dynamic-order-info");

        if (!infoContainer) {
            infoContainer = document.createElement("div");
            infoContainer.id = "dynamic-order-info";
            infoContainer.classList.add("order-info-container");

            const modalTable = modalOverlay.querySelector(".table-responsive");
            modalTable?.parentNode?.insertBefore(infoContainer, modalTable);
        }

        infoContainer.innerHTML = `
            <div class="order-details-summary">
                <h4 class="order-details-title">Información de Entrega</h4>
                <p><strong>Teléfono:</strong> ${order.customerPhone}</p>
                <p><strong>Dirección:</strong> ${order.shippingAddress}</p>
                <p><strong>Notas:</strong> ${order.customerNotes}</p>
                <p class="order-payment-method"><strong>Método de pago:</strong> ${translatePaymentMethod(order.paymentMethod).toUpperCase()}</p>
            </div>
        `;

        const details: IOrderDetail[] = order.orderDetails || [];

        if (details.length === 0) {
            modalItemsBody.innerHTML = `<tr><td colspan="4" class="text-center">No se pudieron cargar los detalles del pedido.</td></tr>`;
        } else {
            details.forEach((detail: IOrderDetail) => {
                const productName = detail.product?.name || "Producto";
                const productPrice = detail.product?.price || 0;
                const subtotal = detail.subtotal || detail.quantity * productPrice;

                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="fw-bold">${productName}</td>
                    <td class="text-center">${detail.quantity} u.</td>
                    <td class="price">${formattedPriceHTML(productPrice)}</td>
                    <td class="price price-cell">${formattedPriceHTML(subtotal)}</td>
                `;
                modalItemsBody.appendChild(tr);
            });
        }

        const oldTotalRow = modalOverlay.querySelector(".checkout-total-row");
        if (oldTotalRow) {
            oldTotalRow.classList.add("hidden"); // Ocultamos con clase en vez de style
        }

        let summaryContainer = modalOverlay.querySelector<HTMLDivElement>("#dynamic-order-summary");

        if (!summaryContainer) {
            summaryContainer = document.createElement("div");
            summaryContainer.id = "dynamic-order-summary";
            summaryContainer.classList.add("order-summary-container");

            const modalTable = modalOverlay.querySelector(".table-responsive");
            modalTable?.parentNode?.insertBefore(summaryContainer, modalTable.nextSibling);
        }

        const subtotalOrder = order.totalPrice;
        const shippingCost = subtotalOrder > 0 ? 500 : 0;
        const finalTotal = subtotalOrder + shippingCost;

        summaryContainer.innerHTML = `
            <div class="order-totals-wrapper">
                <div class="cart__summary-detail">
                    <div class="cart__summary-row">
                        <p>Subtotal</p>
                        <p class="price">${formattedPriceHTML(subtotalOrder)}</p>
                    </div>
                    <div class="cart__summary-row">
                        <p>Envío</p>
                        <p class="price">${formattedPriceHTML(shippingCost)}</p>
                    </div>
                    <hr class="cart__summary-divider">
                    <div class="cart__summary-row cart__summary-row--total">
                        <p>Total</p>
                        <p class="price">${formattedPriceHTML(finalTotal)}</p>
                    </div>
                </div>
            </div>

            <div class="order-status-message">
                <p><strong>${getOrderStatusMessage(order.status)}</p>
            </div>
        `;

        modalOverlay.classList.remove("hidden");
    },

    /**
     * Consulta las órdenes al backend y aplica el filtro perimetral por User ID.
     */
    async refreshOrders(tableBody: HTMLTableSectionElement, container: HTMLElement): Promise<void> {
        try {
            const currentUserId = storage.getUser()?.id;
            if (!currentUserId) {
                dom.tableBody.innerHTML = `<tr><td colspan="6" class="text-center error-text">Error: No se detectó sesión activa.</td></tr>`;
                return;
            }

            // Traemos las órdenes del backend
            const allOrders: IOrder[] = await orderService.getAll();

            // Llenamos el caché maestro y el array de renderizado
            allCustomerOrdersCache = allOrders.filter((order) => order.user?.id === currentUserId);
            customerOrders = [...allCustomerOrdersCache];

            // Renderizamos pasándole el nodo real
            this.renderTable(tableBody, container);
        } catch (error: any) {
            console.error("Error al recuperar el historial de compras:", error);
            tableBody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center error-text">
                        ❌ Error de comunicación con el servidor: ${error.message || "Status no resuelto"}
                    </td>
                </tr>`;
        }
    },
};
