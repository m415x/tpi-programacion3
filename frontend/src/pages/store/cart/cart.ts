import { renderHeader, renderFooter } from "@utils/components";
import { showCart } from "@pages/store/cart/cart.controller";
import { PRODUCTS } from "@/data/data";

// Función principal para inicializar la página del carrito
const initCart = (): void => {
    // Renderizar componentes base
    renderHeader("header");
    renderFooter("footer");

    // Invocar funciones que inyectan los datos de productos
    showCart(PRODUCTS);
};

initCart();
