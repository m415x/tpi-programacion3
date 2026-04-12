import { renderHeader, renderAside, renderFooter } from "@utils/components";

// Inicializar la página del cliente
const initClient = (): void => {
    renderHeader("main-header");
    renderAside("main-sidebar");
    renderFooter("main-footer");
};

initClient();
