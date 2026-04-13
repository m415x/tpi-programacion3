import { renderHeader, renderAside, renderFooter } from "@utils/components";

// Inicializar el panel de administración
const initAdmin = (): void => {
    renderHeader("header");
    renderAside("sidebar");
    renderFooter("footer");
};

initAdmin();
