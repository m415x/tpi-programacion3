import { renderHeader, renderAside, renderFooter } from "@utils/components";
import {
    cargarCategorias,
    cargarProductos,
    cargarSearchBar,
} from "@pages/store/home/logicaTienda";
import { productos, categorias } from "@/data/data";

// Función principal para inicializar la página de la tienda
const initStore = (): void => {
    // Renderizar componentes base
    renderHeader("header");
    renderAside("sidebar");
    renderFooter("footer");

    // Invocar funciones que inyectan los datos de productos y categorías
    cargarCategorias(categorias);
    cargarProductos(productos);
    cargarSearchBar(productos, categorias);
};

initStore();
