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
};
