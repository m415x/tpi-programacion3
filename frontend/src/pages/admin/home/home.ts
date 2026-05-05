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
    renderAside("#sidebar");
    renderFooter("#footer");
};

initAdmin();
