import { userService } from "@services/user.service";
import type { IUser } from "@interfaces/User.interface";

let usersCache: IUser[] = [];

export const usersController = {
    /**
     * Punto de entrada principal invocado por el enrutador de la SPA
     */
    async init(container: HTMLElement): Promise<void> {
        // 1. Inyectamos la maquetación reutilizando tus estilos de tablas administrativas
        container.innerHTML = `
            <section class="main__header">
                <h2>Gestión Completa de Usuarios</h2>
                <p id="users-qty">Cargando usuarios del sistema...</p>
            </section>

            <section class="card table-responsive">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>ID (UUID)</th>
                            <th>Nombre Completo</th>
                            <th>Email / Usuario</th>
                            <th>Rol de Acceso</th>
                            <th>Fecha de Registro</th>
                            <th class="text-center">Acciones</th>
                        </tr>
                    </thead>
                    <tbody id="users-table-body">
                        </tbody>
                </table>
            </section>
        `;

        const tableBody = container.querySelector<HTMLTableSectionElement>("#users-table-body");
        if (!tableBody) return;

        // 2. Cargamos los datos desde Spring Boot
        await this.refreshUsersList(tableBody, container);
    },

    /**
     * Consulta al servidor y dibuja las filas de la tabla
     */
    async refreshUsersList(tableBody: HTMLTableSectionElement, container: HTMLElement): Promise<void> {
        try {
            const allUsers = await userService.getAll();

            usersCache = allUsers.filter((user) => user.userRole === "CLIENT");

            // Actualizamos el contador del header
            const qtyLabel = container.querySelector<HTMLParagraphElement>("#users-qty");
            if (qtyLabel) {
                qtyLabel.textContent = `${usersCache.length} usuarios registrados en la plataforma`;
            }

            this.renderTable(tableBody, container);
        } catch (error: any) {
            console.error("Error al cargar el CRUD de usuarios:", error);
            tableBody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center error-text">
                        ❌ No se pudo conectar con el servicio de usuarios. Verifique los permisos de administrador.
                    </td>
                </tr>`;
        }
    },

    /**
     * Mapea el array de objetos al DOM de la tabla
     */
    renderTable(tableBody: HTMLTableSectionElement, container: HTMLElement): void {
        tableBody.innerHTML = "";

        if (usersCache.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" class="text-center">No hay usuarios registrados en el sistema.</td></tr>`;
            return;
        }

        usersCache.forEach((user: IUser) => {
            const tr = document.createElement("tr");

            const date = new Date(user.createdAt);
            const dateStr = `${date.toLocaleDateString()}`;

            tr.innerHTML = `
                <td class="uuid-cell" title="${user.id}">${user.id.slice(0, 8)}...</td>
                <td class="fw-bold">${user.lastName}, ${user.firstName}</td>
                <td>${user.email}</td>
                <td><span class="badge badge--success-soft">${user.userRole}</span></td>
                <td>${dateStr}</td>
                <td>
                    <div class="actions-wrapper">
                        <button class="btn btn--action btn-quaternary btn-delete-user" data-id="${user.id}">
                            <i class="fa-solid fa-trash-can"></i> Eliminar
                        </button>
                    </div>
                </td>
            `;

            // Vinculamos el evento de eliminación directo en el botón de la fila
            tr.querySelector(".btn-delete-user")?.addEventListener("click", () => {
                this.handleDeleteUser(user.id, tableBody, container);
            });

            tableBody.appendChild(tr);
        });
    },

    /**
     * Procesa la baja del usuario con confirmación nativa sin modales
     */
    async handleDeleteUser(userId: string, tableBody: HTMLTableSectionElement, container: HTMLElement): Promise<void> {
        const confirmar = confirm(
            "¿Estás seguro de que deseas eliminar permanentemente a este usuario? Esta acción no se puede deshacer.",
        );
        if (!confirmar) return;

        try {
            // 1. Pegada al endpoint DELETE de Spring Boot
            await userService.delete(userId);
            alert("Usuario eliminado con éxito del sistema.");

            // 2. Recarga asíncrona optimizada de la lista para actualizar la UI
            await this.refreshUsersList(tableBody, container);
        } catch (error: any) {
            console.error("Error al dar de baja al usuario:", error);
            alert(error.response?.data?.message || "Error perimetral de red al intentar remover el usuario.");
        }
    },
};
