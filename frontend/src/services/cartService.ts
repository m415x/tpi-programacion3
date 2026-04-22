import type { Product } from "@interfaces/Product";
import type { ICartItem } from "@/types/ICartItem";
import { storage } from "@utils/storage";

export const cartService = {
    // Obtiene los productos del carrito filtrando por los IDs almacenados en el localStorage
    getCartItems(products: Product[]): Product[] {
        return products.filter((prod: Product): boolean => {
            const cart: ICartItem[] = storage.getCartItems();

            return cart.some((item: ICartItem) => item.id === prod.id);
        });
    },

    // Obtiene la cantidad total de todos los productos en el carrito.
    getTotalQuantity(): number {
        const cart: ICartItem[] = storage.getCartItems();

        return cart.reduce(
            (acc: number, item: ICartItem): number => acc + item.qty,
            0,
        );
    },

    // Obtiene la cantidad de un producto específico en el carrito.
    getProductQuantity(productId: number): number {
        const cart: ICartItem[] = storage.getCartItems();
        const item = cart.find((i: ICartItem): boolean => i.id === productId);

        return item ? item.qty : 0;
    },
};
