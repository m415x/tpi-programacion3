import type { ICategory } from "@/interfaces/Category.interface";
import {
    showCategoriesInSidebar,
    showProducts,
    showSearchBar,
    initStickySearch,
} from "@pages/store/home/home.controller";
import { categoryService } from "@/services/category.service";
import { renderHeader, renderAside, renderFooter } from "@utils/components";
import { setPageTitle } from "@utils/uiUtils";

/**
 * Función principal asíncrona para inicializar la página de la tienda
 * Conecta los componentes visuales con los servicios de la base de datos JPA
 */
const initStore = async (): Promise<void> => {
    // Establecer el título de la página
    setPageTitle("Tienda");

    try {
        // Traer de forma asíncrona las categorías reales desde Spring Boot
        const activeCategories: ICategory[] = await categoryService.getAll();

        // Renderizar componentes base de la maquetación
        renderHeader("#header");

        renderAside("#sidebar", (): void =>
            // Inyectar las categorías dinámicas en el sidebar lateral
            showCategoriesInSidebar("#sidebar", activeCategories),
        );

        renderFooter("#footer");

        // Invocar controladores de UI (Ambos leen los datos reales del back de manera reactiva)
        await showProducts();
        showSearchBar(activeCategories);

        // Inicializar la funcionalidad de búsqueda sticky al hacer scroll
        initStickySearch();
    } catch (error) {
        console.error("Error catastrófico al inicializar la tienda:", error);

        // Manejo alternativo por si el servidor Java está caído al renderizar la app
        const productContainer = document.querySelector<HTMLElement>("#product-container");
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

initStore();
