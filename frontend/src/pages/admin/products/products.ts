import { renderHeader, renderAside } from "@utils/components";

// Inicializar el panel de administración
const initProductManagement = (): void => {
    renderHeader("header");
    renderAside("sidebar");
};

initProductManagement();
