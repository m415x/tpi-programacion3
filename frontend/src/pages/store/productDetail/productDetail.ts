import { showProductDetail } from "@pages/store/productDetail/productDetail.controller";
import { renderHeader, renderFooter } from "@utils/components";
import { navigate } from "@utils/navigate";
import { PATHS } from "@utils/paths";
import axios from "axios";

/**
 * Función principal para inicializar la página de detalle del producto.
 */
const initProductDetail = async (): Promise<void> => {
    // Renderizar componentes base
    renderHeader("#header");
    renderFooter("#footer");

    // Obtener el ID desde los parámetros de la URL (?id=X)
    const urlParams: URLSearchParams = new URLSearchParams(window.location.search);

    // Validar y convertir el ID a número
    const productId: string = urlParams.get("id") || "";

    // Si el ID es inválido de entrada, volvemos a la tienda
    if (!productId) {
        navigate(PATHS.STORE.HOME);

        return;
    }

    try {
        // Disparamos la renderización y consulta asíncrona al backend
        await showProductDetail(productId);
    } catch (error) {
        // Evaluamos si es un error de respuesta de Axios controlado por el backend
        if (axios.isAxiosError(error) && error.response) {
            const status: number = error.response.status;

            // Si el backend retorna 404 (o el 500 de entidad no encontrada)
            if (status === 404 || status === 500) {
                alert("El producto seleccionado no existe en nuestro catálogo.");
                navigate(PATHS.STORE.HOME);

                return;
            }
        }

        // Si no fue un error controlado de entidad, asumimos caída de red del servidor
        console.error("Error catastrófico de infraestructura:", error);

        const productContainer = document.querySelector<HTMLElement>("#product-detail-container");
        if (productContainer) {
            productContainer.innerHTML = `
                <p class="empty-result">
                    No se pudo conectar con el servicio de gestión de pedidos.
                    Por favor, verifique que el servidor backend esté en ejecución.
                </p>
            `;
        }
    }
};

initProductDetail();
