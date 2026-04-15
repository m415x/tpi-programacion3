import type { Product } from "@interfaces/Product";
import type { ICategory } from "@interfaces/ICategory";

// Promesa global para evitar que se disparen múltiples peticiones a la API
let fastFoodApiPromise: Promise<any> | null = null;

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

    // Obtiene la URL de la imagen de un producto consultando a la API
    async getProductImageUrl(productName: string): Promise<string> {
        const placeholderImg: string =
            "https://via.placeholder.com/400x300?text=Food+Store";

        try {
            // Singleton para la petición: Solo una vez para todos los productos
            if (!fastFoodApiPromise) {
                fastFoodApiPromise = fetch(
                    "https://devsapihub.com/api-fast-food",
                )
                    .then((res: Response): Promise<string> => res.json())
                    .catch((err: Error) => {
                        console.error(
                            "Error consumiendo la API de imágenes:",
                            err,
                        );
                        return [];
                    });
            }

            const dataArray = await fastFoodApiPromise;

            const match = dataArray.find((item: any): boolean => {
                const apiName = (item.name || "").toLowerCase();
                const searchName = productName.toLowerCase();
                return (
                    apiName &&
                    (searchName.includes(apiName) ||
                        apiName.includes(searchName))
                );
            });

            // Fallback: imagen encontrada -> imagen aleatoria -> placeholder
            if (match?.image) return match.image;

            const randomImg =
                dataArray.length > 0
                    ? dataArray[Math.floor(Math.random() * dataArray.length)]
                          .image
                    : placeholderImg;

            return randomImg;
        } catch (error) {
            console.error(`Error asignando imagen a ${productName}:`, error);
            return placeholderImg;
        }
    },
};
