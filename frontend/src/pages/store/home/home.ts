import type { ICategory } from "@interfaces/ICategory";
import {
    showCategoriesInSidebar,
    showProducts,
    showSearchBar,
    initStickySearch,
} from "@pages/store/home/home.controller";
import { renderHeader, renderAside, renderFooter } from "@utils/components";
import { PRODUCTS, getCategories } from "@/data/data";

/**
 * Función principal para inicializar la página de la tienda
 */
const initStore = (): void => {
    // Obtener las categorías activas
    const activeCategories: ICategory[] = getCategories();

    // Renderizar componentes base
    renderHeader("#header");
    renderAside("#sidebar", () =>
        // Inyectar las categorías activas en el sidebar
        showCategoriesInSidebar("#sidebar", activeCategories, PRODUCTS),
    );
    renderFooter("#footer");

    // Invocar funciones que inyectan los datos de productos y categorías
    showProducts(PRODUCTS);
    showSearchBar(PRODUCTS, activeCategories);

    // Inicializar la funcionalidad de búsqueda sticky al hacer scroll
    initStickySearch();
};

initStore();
