import { renderHeader, renderAside } from "@utils/components";

// Inicializar el panel de administración
const initAdmin = (): void => {
    renderHeader("main-header");
    renderAside("main-sidebar");
};

initAdmin();
