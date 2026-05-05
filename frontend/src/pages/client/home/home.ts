import { renderHeader, renderAside, renderFooter } from "@utils/components";
import { setPageTitle } from "@utils/uiUtils";

/**
 * Función principal para inicializar la página de inicio del cliente.
 */
const initClient = (): void => {
    // Establecer el título de la página.
    setPageTitle("Panel de Cliente");

    // Renderizar los componentes de la página: header, sidebar y footer.
    renderHeader("#header");
    renderAside("#sidebar");
    renderFooter("#footer");
};

initClient();
