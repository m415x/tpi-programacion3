import { renderHeader, renderFooter } from "@utils/components";
import { PRODUCTS } from "@/data/data";
import { showProductDetail } from "./productDetail.controller";
import { navigate } from "@utils/navigate";
import { PATHS } from "@utils/paths";
import type { Product } from "@/types/Product";

// Función principal para inicializar la página de Detalle del Producto
const initProductDetail = (): void => {
    // Renderizar componentes base
    renderHeader("header");
    renderFooter("footer");

    // 1. Obtener el ID desde los parámetros de la URL (?id=X)
    const urlParams: URLSearchParams = new URLSearchParams(
        window.location.search,
    );
    const productId: number = parseInt(urlParams.get("id") || "0", 10);

    // 2. Buscar el producto por su identidad (ID)
    const product: Product | undefined = PRODUCTS.find(
        (p: Product): boolean => p.id === productId,
    );

    // 3. Validación de seguridad y existencia
    if (product) {
        showProductDetail(product);
    } else {
        // Si el ID no existe o es inválido, redirigimos a la tienda
        alert("Producto no encontrado.");
        navigate(PATHS.STORE.HOME);
    }
};

initProductDetail();
