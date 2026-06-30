import { OrderStatus, PaymentMethod } from "@interfaces/Enums";
import type { IOrder } from "@interfaces/Order.interface";
import type { IOrderDetail } from "@interfaces/OrderDetail.interface";
import { orderService } from "@services/order.service";
import { userService } from "@services/user.service";
import { formattedPriceHTML, translatePaymentMethod, translateStatusOrder } from "@utils/uiUtils";

// Estado local encapsulado del módulo de administración
let allOrdersCache: IOrder[] = [];
let filteredOrders: IOrder[] = [];
let activeOrderForModal: IOrder | null = null;
let usersMap: Map<string, { name: string; email: string }> = new Map();

// Rastreador simétrico para el ordenamiento dinámico de las columnas por click
let adminOrdersSortDirections: Record<string, "ASC" | "DESC"> = {
    id: "ASC",
    customer: "ASC",
    date: "DESC",
    quantity: "ASC",
    total: "ASC",
    status: "ASC",
};

// Helpers para resolver referencias diferidas del DOM inyectado
const getElements = () => ({
    statusFilter: document.querySelector<HTMLSelectElement>("#admin-order-status-filter")!,
    ordersTableBody: document.querySelector<HTMLTableSectionElement>("#admin-orders-table-body")!,
    modalOverlay: document.querySelector<HTMLDivElement>("#admin-order-detail-modal-overlay")!,
    modalOrderId: document.querySelector<HTMLSpanElement>("#admin-modal-order-id")!,
    modalInfoContainer: document.querySelector<HTMLDivElement>("#admin-modal-dynamic-info")!,
    modalItemsBody: document.querySelector<HTMLTableSectionElement>("#admin-modal-order-items-body")!,
    modalSummaryContainer: document.querySelector<HTMLDivElement>("#admin-modal-dynamic-summary")!,
    modalSelectStatus: document.querySelector<HTMLSelectElement>("#admin-modal-select-status")!,
    btnUpdateStatus: document.querySelector<HTMLButtonElement>("#admin-btn-update-status")!,
    btnCloseModalX: document.querySelector<HTMLButtonElement>("#admin-btn-close-items-modal")!,
});

export const ordersController = {
    /**
     * Punto de entrada invocado por el enrutador de la SPA de administración.
     * Genera la interfaz, vincula ordenamientos, filtros y carga la API relacional.
     */
    async init(targetContainer: HTMLElement): Promise<void> {
        // 1. Inyección de la estructura base semántica y esqueleto de tabla reutilizable
        targetContainer.innerHTML = `
          <div class="admin-container">
            <div class="admin-header-row">
              <div>
                <h2>Gestión de Pedidos Globales</h2>
                <p class="form-help">Panel operativo integral de ventas y estados logísticos</p>
              </div>

              <div class="form-group-filter">
                <!--label for="admin-order-status-filter" class="form-label-inline">Filtrar por estado:</label-->
                <select id="admin-order-status-filter" class="form-select-filter">
                  <option value="ALL">TODOS LOS PEDIDOS</option>
                  <option value="PENDING">PENDIENTE</option>
                  <option value="CONFIRMED">CONFIRMADO</option>
                  <option value="COMPLETED">COMPLETADO</option>
                  <option value="CANCELLED">CANCELADO</option>
                </select>
              </div>
            </div>

            <div class="table-responsive">
              <table class="admin-table">
                <thead>
                  <tr>
                    <th class="sortable-header" data-sort="id">Pedido ID</th>
                    <th class="sortable-header" data-sort="customer">Cliente</th>
                    <th class="sortable-header" data-sort="date">Fecha y hora</th>
                    <th class="sortable-header" data-sort="quantity">Cantidad</th>
                    <th class="sortable-header" data-sort="total">Total</th>
                    <th class="sortable-header" data-sort="status">Estado</th>
                    <th class="text-center">Acciones</th>
                  </tr>
                </thead>
                <tbody id="admin-orders-table-body">
                  <tr><td colspan="7" class="text-center">Sincronizando flujo de pedidos transaccionales...</td></tr>
                </tbody>
              </table>
            </div>
          </div>

          <div id="admin-order-detail-modal-overlay" class="admin-modal-overlay hidden">
            <div class="form-container admin-modal-content">
              <div class="admin-header-row checkout-header-margin">
                <h2>Detalle del Pedido <span id="admin-modal-order-id" class="uuid-cell"></span></h2>
                <button type="button" id="admin-btn-close-items-modal" class="btn-close-modal-x">×</button>
              </div>

              <div id="admin-modal-dynamic-info"></div>

              <div class="table-responsive">
                <table class="admin-table">
                  <thead>
                    <tr>
                      <th>Producto</th>
                      <th class="text-center">Cantidad</th>
                      <th>Precio Unitario</th>
                      <th>Subtotal</th>
                    </tr>
                  </thead>
                  <tbody id="admin-modal-order-items-body"></tbody>
                </table>
              </div>

              <div id="admin-modal-dynamic-summary"></div>
            </div>
          </div>
        `;

        // 2. Inicializar los escuchadores maestros sobre el DOM inyectado en caliente
        this.bindEvents(targetContainer);

        // 3. Traer datos frescos de la API de Spring Boot
        await this.refreshOrders();
    },

    /**
     * Enlaza los filtros, clics de ordenamiento de columnas y confirmaciones PATCH.
     */
    bindEvents(container: HTMLElement): void {
        const dom = getElements();

        // Filtro reactivo por dropdown por estado
        dom.statusFilter.addEventListener("change", () => {
            this.applyFilterAndRender();
        });

        // Escuchador dinámico de clicks en el header del thead para ordenar
        const tableHeader = container.querySelector("thead");
        tableHeader?.addEventListener("click", (e: Event) => {
            const th = (e.target as HTMLElement).closest(".sortable-header");
            if (!th) return;

            const sortBy = th.getAttribute("data-sort")!;
            this.sortOrdersBy(sortBy);
        });

        // Cierre defensivo del modal overlay
        dom.btnCloseModalX.addEventListener("click", () => dom.modalOverlay.classList.add("hidden"));
        dom.modalOverlay.addEventListener("click", (e: Event) => {
            if (e.target === dom.modalOverlay) dom.modalOverlay.classList.add("hidden");
        });
    },

    /**
     * Trae todos los pedidos del backend sin discriminación de usuario y fuerza el orden cronológico.
     */
    async refreshOrders(): Promise<void> {
        const dom = getElements();
        try {
            const [orders, users] = await Promise.all([orderService.getAll(), userService.getAll().catch(() => [])]);

            // Poblamos el mapa de memoria con nombre completo y email
            usersMap.clear();
            users.forEach((u: any) => {
                usersMap.set(u.id, {
                    name: `${u.firstName} ${u.lastName}`,
                    email: u.email,
                });
            });

            // Más recientes primero aprovechando LocalDateTime
            allOrdersCache = orders.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());

            this.applyFilterAndRender();
        } catch (error: any) {
            console.error("Error al recuperar flujo global de pedidos:", error);
            dom.ordersTableBody.innerHTML = `<tr><td colspan="7" class="text-center error-text">❌ Error perimetral: ${error.message}</td></tr>`;
        }
    },

    /**
     * Filtra la memoria caché y manda a dibujar la tabla.
     */
    applyFilterAndRender(): void {
        const dom = getElements();
        const filterValue = dom.statusFilter.value;

        if (filterValue === "ALL" || filterValue === "") {
            filteredOrders = [...allOrdersCache];
        } else {
            filteredOrders = allOrdersCache.filter((order) => order.status === filterValue);
        }

        this.renderTable();
    },

    /**
     * Renderiza las filas basándose de manera estricta en el subconjunto filtrado.
     */
    renderTable(): void {
        const dom = getElements();
        dom.ordersTableBody.innerHTML = "";

        if (filteredOrders.length === 0) {
            dom.ordersTableBody.innerHTML = `<tr><td colspan="7" class="text-center">No se encontraron pedidos globales bajo este criterio.</td></tr>`;
            return;
        }

        filteredOrders.forEach((order: IOrder) => {
            const tr = document.createElement("tr");
            const statusClass = `badge-status--${order.status.toLowerCase()}`;

            // Sumamos las unidades del sub-recurso mapeado
            const qtyProducts = order.orderDetails?.reduce((acc, current) => acc + current.quantity, 0) || 0;

            const date = new Date(order.createdAt);
            const formattedDate = `${date.toLocaleDateString()} - ${date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })} hs`;

            // Buscamos el usuario por su userId plano devuelto por Java
            const customerData = usersMap.get(order.user?.id || "");
            const customerName = customerData ? customerData.name : "Cliente SGP";

            tr.innerHTML = `
                <td><strong class="uuid-cell" title="${order.id}">#${order.id.slice(-6).toUpperCase()}</strong></td>
                <td class="fw-bold">${customerName}</td>
                <td>${formattedDate}</td>
                <td class="text-center">${qtyProducts} u.</td>
                <td class="price-cell">${formattedPriceHTML(order.totalPrice)}</td>
                <td><span class="badge-status ${statusClass}">${translateStatusOrder(order.status)}</span></td>
                <td class="text-center">
                    <button class="btn btn--secondary btn-admin-view">
                        Ver Detalle
                    </button>
                </td>
            `;

            tr.querySelector(".btn-admin-view")?.addEventListener("click", () => {
                this.openDetailsModal(order);
            });

            dom.ordersTableBody.appendChild(tr);
        });
    },

    /**
     * Abre el modal y rellena toda la información del cliente, desgloses y caja PATCH.
     */
    openDetailsModal(order: IOrder): void {
        activeOrderForModal = order;
        const dom = getElements();

        dom.modalOrderId.textContent = `#${order.id.slice(-6).toUpperCase()}`;
        dom.modalItemsBody.innerHTML = "";

        // Buscamos nombre e email para el modal
        const customerData = usersMap.get(order.user?.id || "");
        const customerName = customerData ? customerData.name : "No especificado";
        const customerEmail = customerData ? customerData.email : "N/A";

        // Inyectamos los bloques de Información del Cliente (Usa clases CSS del archivo style.css ya creado)
        dom.modalInfoContainer.innerHTML = `
            <div class="order-details-summary">
                <h4 class="order-details-title">Información de Entrega y Cliente</h4>
                <p><strong>Nombre del Cliente:</strong> ${customerName}</p>
                <p><strong>Email registrado:</strong> ${customerEmail}</p>
                <p><strong>Teléfono de contacto:</strong> ${order.customerPhone}</p>
                <p><strong>Dirección de Entrega:</strong> ${order.shippingAddress}</p>
                <p><strong>Notas del Cliente:</strong> <em>"${order.customerNotes}"</em></p>
                <p class="order-payment-method"><strong>Método de pago:</strong> ${translatePaymentMethod(order.paymentMethod).toUpperCase()}</p>
            </div>
        `;

        // Poblamos la subtabla de ítems relacionales
        const details: IOrderDetail[] = order.orderDetails || [];
        if (details.length === 0) {
            dom.modalItemsBody.innerHTML = `<tr><td colspan="4" class="text-center">No se encontraron productos registrados en esta transacción.</td></tr>`;
        } else {
            details.forEach((detail: IOrderDetail) => {
                const pName = detail.product?.name || "Producto Removido";
                const pPrice = detail.product?.price || 0;
                const subtotal = detail.subtotal || detail.quantity * pPrice;

                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="fw-bold">${pName}</td>
                    <td class="text-center">${detail.quantity} u.</td>
                    <td>${formattedPriceHTML(pPrice)}</td>
                    <td class="price-cell">${formattedPriceHTML(subtotal)}</td>
                `;
                dom.modalItemsBody.appendChild(tr);
            });
        }

        // 3. Calculamos costos y pintamos las herramientas operativas de PATCH
        const subtotalOrder = order.totalPrice;
        const shippingCost = subtotalOrder > 0 ? 500 : 0;
        const finalTotal = subtotalOrder + shippingCost;

        dom.modalSummaryContainer.innerHTML = `
            <div class="order-summary-layout-admin">
                <div class="admin-patch-status-box">
                    <label for="admin-modal-select-status" class="form-label-inline fw-bold">Actualizar flujo logístico:</label>
                    <div class="admin-patch-row">
                        <select id="admin-modal-select-status" class="form-select-filter">
                            <option value="PENDING">PENDIENTE</option>
                            <option value="CONFIRMED">CONFIRMADO</option>
                            <option value="COMPLETED">COMPLETADO</option>
                            <option value="CANCELLED">CANCELADO</option>
                        </select>
                        <button type="button" id="admin-btn-update-status" class="btn btn--tertiary">Actualizar Estado</button>
                    </div>
                </div>

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
            </div>
        `;

        // Pre-seleccionamos el estado que el pedido tiene actualmente en la base de datos
        const selectStatus = dom.modalSummaryContainer.querySelector<HTMLSelectElement>("#admin-modal-select-status")!;
        selectStatus.value = order.status;

        // Vinculamos el click operativo del botón PATCH que acabamos de inyectar
        const btnUpdate = dom.modalSummaryContainer.querySelector<HTMLButtonElement>("#admin-btn-update-status")!;
        btnUpdate.addEventListener("click", () => this.executePatchStatus(order.id, selectStatus.value as OrderStatus));
        dom.modalOverlay.classList.remove("hidden");
    },

    /**
     * Ejecuta la petición PATCH perimetral hacia Spring Boot y refresca los cambios sincrónicamente.
     */
    async executePatchStatus(orderId: string, newStatus: OrderStatus): Promise<void> {
        const dom = getElements();
        try {
            await orderService.updateStatus(orderId, newStatus);
            alert(`¡Pedido #${orderId.slice(-6).toUpperCase()} actualizado con éxito!`);

            dom.modalOverlay.classList.add("hidden");
            await this.refreshOrders();
        } catch (error: any) {
            console.error("Error al aplicar PATCH:", error);
            alert(error.response?.data?.message || "Error al actualizar el estado del pedido.");
        }
    },

    /**
     * Algoritmo de ordenamiento en memoria para las columnas del Dashboard global.
     */
    sortOrdersBy(field: string): void {
        const direction = adminOrdersSortDirections[field];

        filteredOrders.sort((a, b) => {
            let valA: any;
            let valB: any;

            switch (field) {
                case "id":
                    valA = a.id.toLowerCase();
                    valB = b.id.toLowerCase();
                    break;
                case "customer":
                    valA = (a.user?.name || "Cliente SGP").toLowerCase();
                    valB = (b.user?.name || "Cliente SGP").toLowerCase();
                    break;
                case "date":
                    valA = new Date(a.createdAt).getTime();
                    valB = new Date(b.createdAt).getTime();
                    break;
                case "quantity":
                    valA = a.orderDetails?.reduce((acc, curr) => acc + curr.quantity, 0) || 0;
                    valB = b.orderDetails?.reduce((acc, curr) => acc + curr.quantity, 0) || 0;
                    break;
                case "total":
                    valA = a.totalPrice;
                    valB = b.totalPrice;
                    break;
                case "status":
                    valA = a.status.toLowerCase();
                    valB = b.status.toLowerCase();
                    break;
                default:
                    return 0;
            }

            if (valA < valB) return direction === "ASC" ? -1 : 1;
            if (valA > valB) return direction === "ASC" ? 1 : -1;
            return 0;
        });

        // Invertimos el sentido para el próximo click de columna
        adminOrdersSortDirections[field] = direction === "ASC" ? "DESC" : "ASC";
        this.renderTable();
    },
};
