import { initCartEvents, showCart } from "@pages/store/cart/cart.controller";
import { renderHeader, renderFooter } from "@utils/components";
import { setPageTitle } from "@utils/uiUtils";

/**
 * Función principal asíncrona para inicializar la página del carrito de compras
 * Conecta los componentes visuales con los servicios de la base de datos JPA
 * para mostrar los productos en el carrito y gestionar las interacciones del usuario
 * con el carrito de compras, incluyendo actualización de cantidades y eliminación de productos.
 */
const initCart = async (): Promise<void> => {
    // Establecer el título de la página
    setPageTitle("Carrito de Compras");

    try {
        // Renderizar componentes base
        renderHeader("#header");
        renderFooter("#footer");

        // Invocar funciones que inyectan los datos de productos
        await showCart();

        // Inicializar los eventos del carrito
        initCartEvents();
    } catch (error) {
        console.error("Error catastrófico al inicializar el carrito:", error);

        // Manejo alternativo por si el servidor Java está caído al renderizar el carrito
        const cartContainer = document.querySelector<HTMLElement>("#cart-container");
        if (cartContainer) {
            cartContainer.innerHTML = `
                <p class="empty-result">
                    No se pudo conectar con el servicio de gestión de pedidos.
                    Por favor, verifique que el servidor backend esté en ejecución.
                </p>
            `;
        }
    }
};

initCart();
