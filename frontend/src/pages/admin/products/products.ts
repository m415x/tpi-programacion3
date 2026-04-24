import { renderHeader, renderAside } from "@utils/components";

/**
 * Función principal para inicializar la página de gestión de productos.
 */
const initProductManagement = (): void => {
    // Renderizar el encabezado y la barra lateral
    renderHeader("#header");
    renderAside("#sidebar");
};

initProductManagement();
