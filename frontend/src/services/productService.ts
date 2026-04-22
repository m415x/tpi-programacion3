import type { Product } from "@interfaces/Product";
import type { ICategory } from "@interfaces/ICategory";
import { PRODUCTS } from "@/data/data";

const IMAGE_STORAGE_KEY: string = "product_images_map";

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

export const productService = {
    // Filtra productos por ID de categoría
    filterByCategory(
        products: Product[],
        categoryId: number | string,
    ): Product[] {
        const categoryIdStr: string = categoryId.toString();

        return products.filter((prod: Product): boolean =>
            prod.categorias.some((cat: ICategory | undefined): boolean => {
                if (!cat) return false;

                return cat.id.toString() === categoryIdStr;
            }),
        );
    },

    getActiveProducts(products: Product[]): Product[] {
        return products.filter((p: Product): boolean => !p.eliminado);
    },

    // Obtiene una imagen persistente para un producto.
    // Si no existe en el storage, la genera y la guarda.
    getPersistentImage(productId: number): string {
        // 1. Obtener el mapa de imágenes del localStorage
        const storedMap: string | null =
            localStorage.getItem(IMAGE_STORAGE_KEY);
        const imageMap: Record<number, string> = storedMap
            ? JSON.parse(storedMap)
            : {};

        // 2. Si ya existe la imagen para este ID, devolverla
        if (imageMap[productId]) {
            return imageMap[productId];
        }

        // 3. Buscamos el producto para obtener su categoría
        const product: Product | undefined = PRODUCTS.find(
            (p: Product): boolean => p.id === productId,
        );
        const categoryName: string = product?.categorias[0]?.nombre || "food";

        // 4. Traducimos el nombre de la categoría al inglés para la API
        const keyword: string = CATEGORY_TRANSLATIONS[categoryName] || "food";

        // 5. Usamos el endpoint "featured" con keywords y una semilla (sig)
        // Esto garantiza que para el ID 1 siempre devuelva la misma imagen, pero distinta a la del ID 2.
        const newImageUrl = `https://loremflickr.com/500/500/${keyword}/all?lock=${productId}`;

        // 6. Guardar en el mapa y actualizar localStorage
        imageMap[productId] = newImageUrl;
        localStorage.setItem(IMAGE_STORAGE_KEY, JSON.stringify(imageMap));

        return newImageUrl;
    },
};
