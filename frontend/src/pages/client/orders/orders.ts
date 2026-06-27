import { clientOrdersController } from "@pages/client/orders/orders.controller";
import htmlPath from "@pages/client/orders/orders.html?raw"; // Vite carga el HTML como texto crudo
import { renderHeader, renderFooter } from "@utils/components";
import { setPageTitle } from "@utils/uiUtils";

/**
 * Función principal asíncrona para inicializar la página de Mis Pedidos del Cliente.
 * Conecta los componentes visuales con el controlador de órdenes relacionales,
 * renderizando la persistencia histórica de compras del usuario activo.
 */
const initClientOrdersPage = async (): Promise<void> => {
    // Establecer el título de la pestaña en el navegador
    setPageTitle("Mis Pedidos");

    try {
        // 1. Renderizar componentes base perimetrales (Header y Footer estructurados)
        renderHeader("#header");
        renderFooter("#footer");

        // 2. Localizamos el contenedor semántico principal donde vive el layout de la SPA
        const mainContent = document.querySelector<HTMLDivElement>(".main");

        if (mainContent) {
            // 3. Inicializamos el controlador pasándole el contenedor y la plantilla HTML cruda
            await clientOrdersController.init(mainContent);
        }
    } catch (error) {
        console.error("Error catastrófico al inicializar el historial de pedidos:", error);

        // Manejo alternativo defensivo por si el servidor Java está caído o inaccesible
        const mainContent = document.querySelector(".main");
        if (mainContent) {
            mainContent.innerHTML = `
                <div class="admin-container">
                    <p class="empty-result text-center">
                        ⚠️ No se pudo conectar con el servicio de gestión de pedidos históricos.
                        Por favor, verifique que el servidor backend de Spring Boot esté en ejecución.
                    </p>
                </div>
            `;
        }
    }
};

// Se ejecuta de manera inmediata en el ciclo de enrutamiento dinámico de la SPA
initClientOrdersPage();
