import type { ICartItem } from "@interfaces/ICartItem";
import type { Product } from "@interfaces/Product";
import { storage } from "@utils/storage";

/**
 * Servicio para gestionar el carrito de compras, proporcionando funciones para
 * obtener los productos en el carrito, la cantidad total y la cantidad
 * específica de un producto. Este servicio interactúa con el almacenamiento
 * local para obtener los datos del carrito y filtrar los productos correspondientes.
 */
export const cartService = {
    /**
     * Obtiene los productos que están actualmente en el carrito, filtrando la lista
     * @param products Lista completa de productos disponibles
     * @returns Lista de productos que están en el carrito, basada en los IDs
     * almacenados en el carrito.
     */
    getCartItems(products: Product[]): Product[] {
        return products.filter((prod: Product): boolean => {
            // Obtenemos los items del carrito desde el almacenamiento local
            const cart: ICartItem[] = storage.getCartItems();

            // Verificamos si el producto está en el carrito comparando los IDs
            return cart.some((item: ICartItem) => item.id === prod.id);
        });
    },

    /**
     * Obtiene la cantidad total de productos en el carrito sumando las
     * cantidades de cada item.
     * @returns Número total de productos en el carrito.
     */
    getTotalQuantity(): number {
        // Obtenemos los items del carrito desde el almacenamiento local
        const cart: ICartItem[] = storage.getCartItems();

        // Sumamos las cantidades de cada item para obtener el total
        return cart.reduce(
            (acc: number, item: ICartItem): number => acc + item.qty,
            0,
        );
    },

    /**
     * Obtiene la cantidad de un producto específico en el carrito buscando por su ID.
     * @param productId ID del producto.
     * @returns Cantidad del producto en el carrito, o 0 si no está presente.
     */
    getProductQuantity(productId: number): number {
        // Obtenemos los items del carrito desde el almacenamiento local
        const cart: ICartItem[] = storage.getCartItems();
        // Buscamos el item por su ID
        const item = cart.find((i: ICartItem): boolean => i.id === productId);

        return item ? item.qty : 0;
    },
};
