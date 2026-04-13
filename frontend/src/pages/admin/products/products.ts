import { renderHeader, renderAside } from "@utils/components";

// Inicializar el panel de administración
const initProductManagement = (): void => {
    renderHeader("main-header");
    renderAside("main-sidebar");
};

initProductManagement();
