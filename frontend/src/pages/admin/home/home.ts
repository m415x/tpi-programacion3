import { renderHeader, renderAside, renderFooter } from "@utils/components";

/**
 * Función principal para inicializar la página de administración
 */
const initAdmin = (): void => {
    // Renderizar componentes base
    renderHeader("#header");
    renderAside("#sidebar");
    renderFooter("#footer");
};

initAdmin();
