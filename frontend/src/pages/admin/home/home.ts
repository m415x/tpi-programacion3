import { adminController } from "@pages/admin/home/home.controller";
import { renderHeader, renderAside, renderFooter } from "@utils/components";
import { setPageTitle } from "@utils/uiUtils";

/**
 * Función principal para inicializar la página de administración
 */
const initAdmin = (): void => {
    // Establecer el título de la página
    setPageTitle("Panel de Administración");

    // Renderizar componentes base
    renderHeader("#header");
    renderAside("#sidebar", (): void => adminController.showControlPanelInSidebar("#sidebar"));
    renderFooter("#footer");
};

// Aseguramos que corra una vez que el ciclo de scripts de Vite esté asentado
document.addEventListener("DOMContentLoaded", () => {
    initAdmin();
});
