import { renderHeader, renderAside, renderFooter } from "@utils/components";

/**
 * Función principal para inicializar la página de inicio del cliente.
 */
const initClient = (): void => {
    // Renderizar los componentes de la página: header, sidebar y footer.
    renderHeader("#header");
    renderAside("#sidebar");
    renderFooter("#footer");
};

initClient();
