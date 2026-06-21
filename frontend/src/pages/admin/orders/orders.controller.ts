import { OrderStatus } from "@interfaces/Enums";
import type { IOrder } from "@interfaces/Order.interface";
import { orderService } from "@services/order.service";

// Selectores del DOM ficticios para estructurar el módulo
const dom = {
    get statusFilter() {
        return document.querySelector<HTMLSelectElement>("#order-status-filter")!;
    },
    get ordersTableBody() {
        return document.querySelector<HTMLTableSectionElement>("#orders-table-body")!;
    },
    // Elementos del Modal/Pop-up
    get orderModal() {
        return document.querySelector<HTMLDivElement>("#order-detail-modal")!;
    },
    get modalOrderId() {
        return document.querySelector<HTMLSpanElement>("#modal-order-id")!;
    },
    get modalCustomerInfo() {
        return document.querySelector<HTMLParagraphElement>("#modal-customer-info")!;
    },
    get modalOrderSelectStatus() {
        return document.querySelector<HTMLSelectElement>("#modal-order-status")!;
    },
    get btnSaveStatus() {
        return document.querySelector<HTMLButtonElement>("#btn-save-order-status")!;
    },
    get btnCloseModal() {
        return document.querySelector<HTMLButtonElement>("#btn-close-modal")!;
    },
};

let allOrders: IOrder[] = []; // Cache local de las órdenes cargadas

export const ordersController = {
    /**
     * Inicializa el controlador de administración de pedidos.
     */
    async init(): Promise<void> {
        if (!dom.statusFilter || !dom.ordersTableBody) return;

        this.bindEvents();
        await this.refreshTable();
    },

    /**
     * Registra los escuchadores de eventos para los filtros y el modal.
     */
    bindEvents(): void {
        // Filtro por estado a través del input select
        dom.statusFilter.addEventListener("change", () => {
            this.renderTable(dom.statusFilter.value);
        });

        // Guardar el nuevo estado desde el pop-up
        dom.btnSaveStatus.addEventListener("click", async () => {
            const orderId = dom.modalOrderId.textContent;
            const newStatus = dom.modalOrderSelectStatus.value as OrderStatus;

            if (!orderId) return;

            try {
                // Actualización en la base de datos a través de la API
                await orderService.updateStatus(orderId, newStatus);
                alert(`Pedido #${orderId} actualizado exitosamente a ${newStatus}.`);

                this.closeModal();
                await this.refreshTable(); // Recarga los datos de la API y vuelve a renderizar
            } catch (error: any) {
                alert(error.response?.data?.message || "Error al actualizar el estado del pedido.");
            }
        });

        // Cerrar el pop-up
        dom.btnCloseModal.addEventListener("click", () => this.closeModal());
    },

    /**
     * Trae las órdenes actualizadas desde el backend.
     */
    async refreshTable(): Promise<void> {
        try {
            allOrders = await orderService.getAll();
            // Aplica el filtro que esté seleccionado actualmente
            this.renderTable(dom.statusFilter.value);
        } catch (error) {
            console.error("Error al obtener los pedidos:", error);
        }
    },

    /**
     * Renderiza las filas de la tabla aplicando el filtro seleccionado.
     */
    renderTable(filterStatus: string): void {
        dom.ordersTableBody.innerHTML = "";

        // Si el filtro es vacío o "ALL", muestra todos; si no, filtra por la propiedad correspondente
        const filtered =
            filterStatus === "" || filterStatus === "ALL"
                ? allOrders
                : allOrders.filter((o) => o.status === filterStatus);

        if (filtered.length === 0) {
            dom.ordersTableBody.innerHTML = `<tr><td colspan="6" class="text-center">No hay pedidos que coincidan con el criterio.</td></tr>`;
            return;
        }

        filtered.forEach((order) => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td><strong>#${order.id.slice(-6)}</strong></td>
                <td>${order.user?.name || "Cliente Invitado"}</td>
                <td>$${order.totalPrice.toFixed(2)}</td>
                <td><span class="badge badge-${order.status.toLowerCase()}">${order.status}</span></td>
                <td>${new Date(order.createdAt).toLocaleDateString()}</td>
                <td>
                    <button class="btn btn-sm btn-primary btn-view-order" data-id="${order.id}">
                        Ver Detalle / Editar
                    </button>
                </td>
            `;

            // Vincular el evento para abrir el Pop-up
            tr.querySelector(".btn-view-order")?.addEventListener("click", () => {
                this.openModal(order);
            });

            dom.ordersTableBody.appendChild(tr);
        });
    },

    /**
     * Abre el pop-up con los detalles del pedido y la inconsistencia subsanada (Tel, Dir, Notas).
     */
    openModal(order: IOrder): void {
        dom.modalOrderId.textContent = order.id;
        dom.modalOrderSelectStatus.value = order.status;

        // Aquí integramos los nuevos campos solicitados por el docente en la visualización del detalle
        dom.modalCustomerInfo.innerHTML = `
            <strong>Cliente:</strong> ${order.user?.name || "N/A"}<br>
            <strong>Email:</strong> ${order.user?.email || "N/A"}<br>
            <strong>Teléfono:</strong> ${order.customerPhone || "No provisto"}<br>
            <strong>Dirección de Entrega:</strong> ${order.shippingAddress || "Retiro en local"}<br>
            <strong>Notas del Cliente:</strong> <em>"${order.customerNotes || "Sin notas adicionales."}"</em>
        `;

        dom.orderModal.classList.add("modal-open"); // O dom.orderModal.style.display = "block"
    },

    /**
     * Cierra el pop-up reseteando sus campos.
     */
    closeModal(): void {
        dom.orderModal.classList.remove("modal-open");
        dom.modalOrderId.textContent = "";
        dom.modalCustomerInfo.innerHTML = "";
    },
};
