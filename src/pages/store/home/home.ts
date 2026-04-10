import { renderHeader, renderAside, renderFooter } from "@utils/components";
import {
    cargarCategorias,
    cargarProductos,
    cargarSearchBar,
} from "@pages/store/home/logicaTienda";
import { productos, categorias } from "@/js/data";

// Función principal para inicializar la página de la tienda
const initStore = (): void => {
    // Renderizar componentes base
    renderHeader("main-header");
    renderAside("main-sidebar");
    renderFooter("main-footer");

    // Invocar funciones que inyectan los datos de productos y categorías
    cargarCategorias(categorias);
    cargarProductos(productos);
    cargarSearchBar(productos, categorias);
};

document.addEventListener("DOMContentLoaded", initStore);
