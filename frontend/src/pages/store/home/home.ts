import { renderHeader, renderAside, renderFooter } from "@utils/components";
import {
    showCategoriesInSidebar,
    showProducts,
    showSearchBar,
} from "@pages/store/home/home.controller";
import { PRODUCTS, getCategories } from "@/data/data";
import type { ICategory } from "@interfaces/ICategory";

// Función principal para inicializar la página de la tienda
const initStore = (): void => {
    // Obtener las categorías activas primero para inyectarlas al aside
    const activeCategories: ICategory[] = getCategories();

    // Renderizar componentes base
    renderHeader("header");
    renderAside("sidebar", () =>
        showCategoriesInSidebar("sidebar", activeCategories, PRODUCTS),
    );
    renderFooter("footer");

    // Invocar funciones que inyectan los datos de productos y categorías
    showProducts(PRODUCTS);
    showSearchBar(PRODUCTS, activeCategories);
};

initStore();
