import { dashboardController } from "@pages/admin/dashboard/dashboard.controller";
import { categoriesController } from "@pages/admin/categories/categories.controller";
import { productsController } from "@pages/admin/products/products.controller";
import { ordersController } from "@pages/admin/orders/orders.controller";
import { PATHS } from "@utils/paths";

export const adminController = {
    /**
     *
     * @param containerSelector Selector CSS del contenedor de la Sidebar
     * @param options Lista de
     */
    showControlPanelInSidebar: (containerSelector: string): void => {
        const container = document.querySelector<HTMLElement>(containerSelector);
        if (!container) return;

        container.innerHTML = `
            <div class="sidebar__header">
                <h2>Administración</h2>
                <p>Panel de control</p>
            </div>

            <ul id="control-option-list" class="sidebar__list">
                <li>
                    <a href="#" class="link link--active" data-option="dashboard">
                        <i class="fa-solid fa-chart-line sidebar__icon"></i>
                        <span>Dashboard</span>
                    </a>
                </li>
                <li>
                    <a href="#" class="link" data-option="categories">
                        <i class="fa-solid fa-folder sidebar__icon"></i>
                        <span>Categorías</span>
                    </a>
                </li>
                <li>
                    <a href="#" class="link" data-option="products">
                        <i class="fa-solid fa-burger sidebar__icon"></i>
                        <span>Productos</span>
                    </a>
                </li>
                <li>
                    <a href="#" class="link" data-option="orders">
                        <i class="fa-solid fa-box sidebar__icon"></i>
                        <span>Pedidos</span>
                    </a>
                </li>
            </ul>

            <div class="sidebar__footer">
                <a href="${PATHS.STORE.HOME}" class="link sidebar__exit-link">
                    <i class="fa-solid fa-store sidebar__icon"></i>
                    <span>Ver Tienda</span>
                </a>
            </div>
        `;

        const dashboard = container.querySelector<HTMLLinkElement>("[data-option='dashboard']");
        const categories = container.querySelector<HTMLLinkElement>("[data-option='categories']");
        const products = container.querySelector<HTMLLinkElement>("[data-option='products']");
        const orders = container.querySelector<HTMLLinkElement>("[data-option='orders']");
        const optionList = container.querySelector<HTMLUListElement>("#control-option-list");
        if (!dashboard || !categories || !products || !orders || !optionList) return;

        /**
         * Delegación de eventos centralizada para evitar agregar múltiples listeners individuales
         */
        optionList.addEventListener("click", async (e: Event) => {
            e.preventDefault();
            const target = e.target as HTMLElement;

            const anchor = target.closest<HTMLLinkElement>("[data-option]");
            if (!anchor) return;

            const option = anchor.getAttribute("data-option");
            if (!option) return;

            // 1. Gestionar las clases activas visualmente en el menú
            optionList
                .querySelectorAll(".link")
                .forEach((link: Element): void => link.classList.remove("link--active"));
            anchor.classList.add("link--active");

            // 2. Obtener el contenedor principal donde se inyectará la SPA
            const mainContent = document.getElementById("admin-main-content");
            if (!mainContent) return;

            // Limpiamos el contenedor para recibir el nuevo módulo
            mainContent.innerHTML = "";

            // 3. Enrutador interno asíncrono (Inyección de módulos reactivos)
            try {
                switch (option) {
                    case "dashboard":
                        await dashboardController.init(mainContent);
                        break;
                    case "categories":
                        await categoriesController.init(mainContent);
                        break;
                    case "products":
                        await productsController.init(mainContent);
                        break;
                    case "orders":
                        await ordersController.init();
                        break;
                }
            } catch (error) {
                console.error(`Error al inyectar el módulo ${option}:`, error);
            }
        });

        // Forzamos la carga del Dashboard de forma asíncrona inmediata al renderizar la vista base
        const mainContent = document.getElementById("admin-main-content");
        if (mainContent) {
            dashboardController.init(mainContent).catch((err) => {
                console.error("Error cargando dashboard inicial:", err);
            });
        }
    },
};
