import { renderHeader, renderAside, renderFooter } from "@utils/components";

// Inicializar la página del cliente
const initClient = (): void => {
    renderHeader("header");
    renderAside("sidebar");
    renderFooter("footer");
};

initClient();
