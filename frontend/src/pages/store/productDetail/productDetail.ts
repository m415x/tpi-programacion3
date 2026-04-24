import type { Product } from "@interfaces/Product";
import { showProductDetail } from "@pages/store/productDetail/productDetail.controller";
import { productService as ps } from "@services/productService";
import { renderHeader, renderFooter } from "@utils/components";
import { navigate } from "@utils/navigate";
import { PATHS } from "@utils/paths";

/**
 * Función principal para inicializar la página de detalle del producto.
 */
const initProductDetail = (): void => {
    // Renderizar componentes base
    renderHeader("#header");
    renderFooter("#footer");

    // Obtener el ID desde los parámetros de la URL (?id=X)
    const urlParams: URLSearchParams = new URLSearchParams(
        window.location.search,
    );

    // Validar y convertir el ID a número
    const productId: number = parseInt(urlParams.get("id") || "0", 10);

    // Buscar el producto por su id
    const product: Product | undefined = ps.getProductById(productId);

    // Si el producto existe, mostrar su detalle
    if (product) {
        showProductDetail(product);
    } else {
        // Si el ID no existe o es inválido, redirigimos a la tienda
        alert("Producto no encontrado.");
        navigate(PATHS.STORE.HOME);
    }
};

initProductDetail();
