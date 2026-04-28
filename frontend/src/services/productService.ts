import type { ICategory } from "@interfaces/ICategory";
import type { Product } from "@interfaces/Product";
import { PRODUCTS } from "@/data/data";

// Clave de almacenamiento para el mapa de imágenes persistentes
const IMAGE_STORAGE_KEY: string = "productImages";

// Diccionario traductor para términos de búsqueda precisos
const CATEGORY_TRANSLATIONS: Record<string, string> = {
    Pizzas: "pizza",
    Hamburguesas: "burger",
    Bebidas: "soda,drink",
    Postres: "dessert,cake,pie",
    Empanadas: "pasty,dumpling",
    Ensaladas: "salad",
    "Papas Fritas": "french-fries",
};

/**
 * Servicio para gestionar la lógica relacionada con los productos, incluyendo
 * filtrado por categoría, obtención de productos activos y generación de imágenes
 * persistentes para cada producto utilizando la API de LoremFlickr.
 */
export const productService = {
    /**
     * Filtra los productos por categoría, comparando el ID de la categoría con
     * los IDs de las categorías asociadas a cada producto. El ID de categoría
     * puede ser un número o una cadena, y se convierte a cadena para la comparación.
     * @param products Lista completa de productos disponibles
     * @param categoryId ID de la categoría por la cual filtrar, puede ser número o cadena
     * @returns Lista de productos que pertenecen a la categoría especificada.
     */
    filterByCategory(
        products: Product[],
        categoryId: number | string,
    ): Product[] {
        // Casteamos el categoryId a string para compararlo con los IDs de las categorías
        const categoryIdStr: string = categoryId.toString();

        // Filtramos los productos que tienen al menos una categoría cuyo ID
        // coincide con categoryIdStr
        return products.filter((prod: Product): boolean =>
            prod.categorias.some((cat: ICategory | undefined): boolean => {
                if (!cat) return false;

                return cat.id.toString() === categoryIdStr;
            }),
        );
    },

    /**
     * Obtiene solo los productos que no están marcados como eliminados,
     * filtrando la lista completa.
     * @param products Lista completa de productos disponibles
     * @returns Lista de productos que tienen el estado "eliminado" en false.
     */
    getActiveProducts(products: Product[]): Product[] {
        return products.filter((p: Product): boolean => !p.eliminado);
    },

    /**
     * Genera una URL de imagen persistente para un producto dado su ID,
     * utilizando la API de LoremFlickr con una semilla basada en el ID del producto.
     * @param productId ID del producto para el cual generar la imagen persistente.
     * @returns URL de la imagen generada, que se mantiene constante para el mismo
     * ID de producto.
     */
    getPersistentImage(productId: number): string {
        // Obtener el mapa de imágenes del localStorage
        const storedMap: string | null =
            localStorage.getItem(IMAGE_STORAGE_KEY);

        // Si no existe, inicializamos un objeto vacío. Si existe, lo parseamos.
        const imageMap: Record<number, string> = storedMap
            ? JSON.parse(storedMap)
            : {};

        // Si ya existe la imagen para este ID, devolverla
        if (imageMap[productId]) {
            return imageMap[productId] as string;
        }

        // Buscamos el producto para obtener su categoría
        const product: Product | undefined = PRODUCTS.find(
            (p: Product): boolean => p.id === productId,
        );

        // Obtenemos el nombre de la primera categoría del producto, o "food"
        // si no tiene categorías
        const categoryName: string = product?.categorias[0]?.nombre || "food";

        // Traducimos el nombre de la categoría al inglés para la API
        const keyword: string = CATEGORY_TRANSLATIONS[categoryName] || "food";

        // Generamos la URL de la imagen utilizando la categoría traducida y el
        // ID del producto como semilla
        const newImageUrl = `https://loremflickr.com/500/500/${keyword}/all?lock=${productId}`;

        // Guardamos en el mapa y actualizar localStorage
        imageMap[productId] = newImageUrl;
        localStorage.setItem(IMAGE_STORAGE_KEY, JSON.stringify(imageMap));

        return newImageUrl;
    },

    /**
     * Busca un producto por su ID en la lista de productos.
     * @param id ID del producto que se desea obtener.
     * @return El producto que coincide con el ID proporcionado, o undefined si no se encuentra.
     */
    getProductById(id: number): Product | undefined {
        return PRODUCTS.find((p: Product): boolean => p.id === id);
    },
};
