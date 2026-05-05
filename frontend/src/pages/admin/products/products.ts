import { renderHeader, renderAside } from "@utils/components";
import { setPageTitle } from "@utils/uiUtils";

/**
 * Función principal para inicializar la página de gestión de productos.
 */
const initProductManagement = (): void => {
    // Establecer el título de la página
    setPageTitle("Gestión de Productos");

    // Renderizar el encabezado y la barra lateral
    renderHeader("#header");
    renderAside("#sidebar");
};

initProductManagement();
