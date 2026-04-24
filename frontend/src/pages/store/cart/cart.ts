import { initCartEvents, showCart } from "@pages/store/cart/cart.controller";
import { renderHeader, renderFooter } from "@utils/components";
import { PRODUCTS } from "@/data/data";

/**
 * Función principal para inicializar la página de Carrito de Compras
 */
const initCart = (): void => {
    // Renderizar componentes base
    renderHeader("#header");
    renderFooter("#footer");

    // Invocar funciones que inyectan los datos de productos
    showCart(PRODUCTS);

    // Inicializar los eventos del carrito
    initCartEvents(PRODUCTS);
};

initCart();
