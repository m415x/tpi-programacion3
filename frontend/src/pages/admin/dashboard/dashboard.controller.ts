import { categoryService } from "@services/category.service";
import { orderService } from "@services/order.service";
import { productService } from "@services/product.service";
import { userService } from "@services/user.service";

export const dashboardController = {
    /**
     * Punto de entrada de la SPA para la pestaña de estadísticas e informes del Admin.
     */
    async init(targetContainer: HTMLElement): Promise<void> {
        // 1. Inyectamos la estructura base del esqueleto del Dashboard
        targetContainer.innerHTML = `
            <div class="admin-header-row">
                <h2>Dashboard de Control</h2>
                <span class="badge badge--info" id="dashboard-date">Cargando métricas...</span>
            </div>

            <div class="dashboard-kpi-grid">
                <div class="kpi-card kpi-card--orders kpi-card--clickable" data-nav-option="orders">
                    <div class="kpi-card__title">Pedidos Totales ↗</div>
                    <div class="kpi-card__value" id="kpi-orders">-</div>
                    <div class="kpi-card__footer">Órdenes históricas registradas</div>
                </div>

                <div class="kpi-card kpi-card--clickable" data-nav-option="products">
                    <div class="kpi-card__title">Total Productos ↗</div>
                    <div class="kpi-card__value" id="kpi-products">-</div>
                    <div class="kpi-card__footer">En el catálogo actual</div>
                </div>

                <div class="kpi-card kpi-card--warning">
                    <div class="kpi-card__title">Stock Alerta</div>
                    <div class="kpi-card__value" id="kpi-low-stock">-</div>
                    <div class="kpi-card__footer">Productos con 3 unidades o menos</div>
                </div>

                <div class="kpi-card kpi-card--success kpi-card--clickable" data-nav-option="categories">
                    <div class="kpi-card__title">Categorías Activas ↗</div>
                    <div class="kpi-card__value" id="kpi-categories">-</div>
                    <div class="kpi-card__footer">Secciones del menú habilitadas</div>
                </div>

                <div class="kpi-card kpi-card--info">
                    <div class="kpi-card__title">Cliente Destacado</div>
                    <div class="kpi-card__value kpi-card__value--text" id="kpi-top-user">Cargando...</div>
                    <div class="kpi-card__footer">Mayor volumen de pedidos</div>
                </div>
            </div>

            <div class="dashboard-panels-row">
                <div class="dashboard-panel">
                    <h3>⚠️ Alerta Crítica de Reposición</h3>
                    <div class="table-responsive">
                        <table class="admin-table">
                            <thead>
                                <tr>
                                    <th>Producto</th>
                                    <th>Precio</th>
                                    <th>Stock Actual</th>
                                </tr>
                            </thead>
                            <tbody id="dashboard-low-stock-table">
                                <tr><td colspan="3" class="text-center">Analizando inventario...</td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        `;

        // Colocamos la fecha actual en la cabecera
        const dateSpan = document.getElementById("dashboard-date");
        if (dateSpan) dateSpan.textContent = `Informe al: ${new Date().toLocaleDateString()}`;

        // 2. Cargamos las métricas dinámicas pegándole a la API de Spring Boot
        await this.loadMetrics();

        // 3. Inicializamos los escuchadores de eventos para redirección interna de las tarjetas
        this.initDashboardEvents();
    },

    /**
     * Consulta los servicios en paralelo y puebla los contadores del DOM.
     */
    async loadMetrics(): Promise<void> {
        try {
            // Referencias del DOM con aserción estricta no nula
            const kpiOrders = document.getElementById("kpi-orders")!;
            const kpiProducts = document.getElementById("kpi-products")!;
            const kpiLowStock = document.getElementById("kpi-low-stock")!;
            const kpiCategories = document.getElementById("kpi-categories")!;
            const kpiTopUser = document.getElementById("kpi-top-user")!;
            const lowStockTable = document.getElementById("dashboard-low-stock-table")!;

            // 🚀 EJECUCIÓN PARALELA OPTIMIZADA: Sumamos al cliente estrella en el Promise.all
            // Usamos un bloque defensivo interno para el top customer por si la BD de órdenes está vacía al inicio
            const [orders, products, categories, topUser] = await Promise.all([
                orderService.getAll().catch(() => []),
                productService.getAll(),
                categoryService.getAll(),
                userService.getTopCustomer().catch((err) => {
                    console.warn("Aviso: No se encontraron órdenes o usuarios para calcular el top customer aún.", err);
                    return null;
                }),
            ]);

            // 1. Calcular totales del catálogo
            kpiOrders.textContent = orders.length.toString();
            kpiProducts.textContent = products.length.toString();
            kpiCategories.textContent = categories.length.toString();

            // 2. Filtrar productos con stock crítico (bajo o igual a 3 unidades)
            const lowStockItems = products.filter((p) => p.stock <= 3);
            kpiLowStock.textContent = lowStockItems.length.toString();

            // 3. Poblar tabla rápida de alerta de reposición
            lowStockTable.innerHTML = "";
            if (lowStockItems.length === 0) {
                lowStockTable.innerHTML = `<tr><td colspan="3" class="text-center" style="color: #2e7d32; font-weight: bold;">✅ Todo el stock se encuentra óptimo.</td></tr>`;
            } else {
                lowStockItems.forEach((item) => {
                    const tr = document.createElement("tr");
                    tr.innerHTML = `
                        <td class="fw-bold">${item.name}</td>
                        <td class="price-cell">$${item.price.toFixed(2)}</td>
                        <td>
                            <span class="stock-indicator stock-low">${item.stock} u.</span>
                        </td>
                    `;
                    lowStockTable.appendChild(tr);
                });
            }

            // 4. Inyectar dinámicamente el Cliente Destacado real devuelto por Spring Boot
            if (topUser && topUser.firstName) {
                kpiTopUser.textContent = `${topUser.firstName} ${topUser.lastName.substring(0, 1)}.`;
                kpiTopUser.style.fontSize = ""; // Resetea tamaño por defecto si cambió previamente
            } else {
                kpiTopUser.textContent = "Sin datos";
                kpiTopUser.style.fontSize = "1.2rem";
            }
        } catch (error) {
            console.error("Error crítico al poblar las métricas base del Dashboard:", error);
        }
    },

    /**
     * ENLAZADOR DE EVENTOS PARA CAPTURAR LOS CLICKS EN LAS KPI CARDS
     * Busca el enlace correspondiente en el Sidebar para disparar su click nativo y
     * sincronizar las clases .link--active de tu home.controller.ts automáticamente.
     */
    initDashboardEvents(): void {
        const grid = document.querySelector(".dashboard-kpi-grid");
        if (!grid) return;

        grid.addEventListener("click", (e: Event) => {
            const target = e.target as HTMLElement;
            const clickableCard = target.closest<HTMLElement>("[data-nav-option]");

            if (!clickableCard) return;

            const optionTarget = clickableCard.getAttribute("data-nav-option");
            if (!optionTarget) return;

            // Buscamos el elemento anchor original dentro del listado de la barra lateral (Sidebar)
            const sidebarAnchor = document.querySelector<HTMLLinkElement>(
                `#control-option-list [data-option="${optionTarget}"]`,
            );

            if (sidebarAnchor) {
                // 💥 DISPARO MAESTRO: Ejecutamos el click del menú. Esto limpia el mainContent,
                // activa la clase naranja .link--active e inyecta la nueva vista sin repetir código.
                sidebarAnchor.click();
            }
        });
    },
};
