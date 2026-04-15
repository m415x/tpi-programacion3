import type { IUser } from "@interfaces/IUser";
import type { ICartItem } from "@/types/ICartItem";
import { PRODUCTS } from "@/data/data";
import type { Product } from "@/types/Product";

// Store centralizado para gestionar el estado de la sesión
export const storage = {
    // --- Funciones para gestionar usuarios ---

    // Obtener usuarios
    getUsers(): IUser[] {
        const users: string | null = localStorage.getItem("users");
        return users ? JSON.parse(users) : [];
    },

    // Guardar la lista completa
    saveUsers(users: IUser[]): void {
        localStorage.setItem("users", JSON.stringify(users));
    },

    // --- Funciones para gestionar la sesión actual ---

    // Obtener el usuario actual
    getUser(): IUser | null {
        const data: string | null = localStorage.getItem("userData");
        return data ? JSON.parse(data) : null;
    },

    // Guardar sesión
    setUser(user: IUser): void {
        localStorage.setItem("userData", JSON.stringify(user));
    },

    // Eliminar sesión
    clear(): void {
        localStorage.removeItem("userData");
    },

    // Verificar si hay una sesión activa
    isAuthenticated(): boolean {
        return this.getUser() !== null;
    },

    // Obtener el rol rápidamente
    getRole(): string | null {
        const user: IUser | null = this.getUser();
        return user ? user.role : null;
    },

    // --- Funciones para gestionar carrito ---

    // Obtener items
    getCartItems(): ICartItem[] {
        const cartItems: string | null = localStorage.getItem("cartItems");
        return cartItems ? JSON.parse(cartItems) : [];
    },

    // Guardar items
    updateCartItem(id: number): boolean {
        const cartItems: ICartItem[] = this.getCartItems();

        // Buscar producto y validar existencia
        const product: Product | undefined = PRODUCTS.find(
            (p: Product): boolean => p.id === id,
        );
        if (!product) return false;

        const existingItem: ICartItem | undefined = cartItems.find(
            (i: ICartItem): boolean => i.id === id,
        );

        const currentQty: number = existingItem ? existingItem.qty : 0;

        // Cláusula de guarda para el Stock
        if (currentQty >= product.stock) {
            return false;
        }

        // Lógica de actualización
        if (existingItem) {
            existingItem.qty++;
        } else {
            cartItems.push({ id, qty: 1 });
        }

        localStorage.setItem("cartItems", JSON.stringify(cartItems));
        return true;
    },

    // Disminuir cantidad de un item en el carrito
    decreaseCartItem(id: number): void {
        const cartItems: ICartItem[] = this.getCartItems();

        const item: ICartItem | undefined = cartItems.find(
            (i: ICartItem): boolean => i.id === id,
        );

        // Cláusula de guarda: si no existe el item, no hacemos nada
        if (!item) return;

        if (item.qty > 1) {
            item.qty--;
            localStorage.setItem("cartItems", JSON.stringify(cartItems));
        }
    },

    // Eliminar un item del carrito
    removeCartItem(id: number): void {
        const cartItems: ICartItem[] = this.getCartItems();
        // Creamos un nuevo array sin el producto a eliminar
        const updatedCart = cartItems.filter((i) => i.id !== id);

        localStorage.setItem("cartItems", JSON.stringify(updatedCart));
    },

    // Limpiar carrito
    clearCart(): void {
        localStorage.removeItem("cartItems");
    },
};
