import type { IUser } from "@interfaces/IUser";
import type { ICartItem } from "@interfaces/ICartItem";
import { PRODUCTS } from "@/data/data";
import type { Product } from "@interfaces/Product";
import type { Role } from "@interfaces/Role";

/**
 * Este objeto contiene un store centralizado para gestionar el estado de la
 * sesión, incluyendo usuarios y carrito de compras. Proporciona funciones para
 * obtener, guardar y eliminar datos relacionados con la autenticación y el
 * carrito, utilizando localStorage como mecanismo de persistencia.
 */
export const storage = {
    // --- Funciones para gestionar usuarios ---

    /**
     * Obtiene la lista completa de usuarios almacenados en localStorage. Si no
     * hay usuarios.
     * @returns Un array de objetos IUser o un array vacío si no hay usuarios almacenados.
     */
    getUsers(): IUser[] {
        const users: string | null = localStorage.getItem("users");
        return users ? JSON.parse(users) : [];
    },

    /**
     * Guarda una lista de usuarios en localStorage. Reemplaza cualquier lista
     * anterior almacenada bajo la clave "users".
     * @param users Un array de objetos IUser que se desea almacenar.
     * Este array se convertirá a una cadena JSON antes de guardarse.
     */
    saveUsers(users: IUser[]): void {
        localStorage.setItem("users", JSON.stringify(users));
    },

    // --- Funciones para gestionar la sesión actual ---

    /**
     * Obtiene los datos del usuario actualmente autenticado desde localStorage.
     * @returns El objeto IUser del usuario en sesión o null si no hay sesión activa.
     */
    getUser(): IUser | null {
        const data: string | null = localStorage.getItem("userData");
        return data ? JSON.parse(data) : null;
    },

    /**
     * Guarda los datos del usuario actualmente autenticado en localStorage bajo la
     * clave "userData". Esto se utiliza para mantener la sesión activa entre recargas
     * de página.
     * @param user Un objeto IUser que representa al usuario que se desea mantener en
     * sesión. Este objeto se convertirá a una cadena JSON antes de guardarse en localStorage.
     */
    setUser(user: IUser): void {
        localStorage.setItem("userData", JSON.stringify(user));
    },

    /**
     * Elimina los datos del usuario actualmente autenticado de localStorage, cerrando
     * la sesión activa.
     */
    clear(): void {
        localStorage.removeItem("userData");
    },

    /**
     * Verifica si hay un usuario actualmente autenticado al comprobar si getUser() devuelve
     * un objeto válido en lugar de null. Esto se utiliza para determinar si se debe mostrar
     * contenido protegido o redirigir a la página de inicio de sesión.
     * @returns true si hay un usuario autenticado, false si no lo hay.
     */
    isAuthenticated(): boolean {
        return this.getUser() !== null;
    },

    /**
     * Obtiene el rol del usuario actualmente autenticado.
     * @returns El rol del usuario autenticado o null si no hay sesión activa.
     */
    getRole(): Role | null {
        const user: IUser | null = this.getUser();
        return user ? user.role : null;
    },

    // --- Funciones para gestionar carrito ---

    /**
     * Obtiene los items actualmente almacenados en el carrito desde localStorage.
     * @returns Un array de objetos ICartItem que representan los items en el carrito
     * o un array vacío si no hay items almacenados.
     */
    getCartItems(): ICartItem[] {
        const cartItems: string | null = localStorage.getItem("cartItems");
        return cartItems ? JSON.parse(cartItems) : [];
    },

    /**
     * Agrega un producto al carrito o actualiza su cantidad si ya existe. Si se proporciona
     * fixedQty, se establece esa cantidad específica para el producto. Si no, se incrementa
     * la cantidad actual del producto en el carrito en 1.
     * @param id El ID del producto que se desea agregar o actualizar en el carrito.
     * @param fixedQty Opcional. Si se proporciona, se establece esta cantidad
     * específica para el producto en el carrito.
     * @returns true si el producto se agregó o actualizó exitosamente en el carrito,
     * false si no se pudo agregar o actualizar debido a restricciones de stock o si el producto no existe.
     */
    updateCartItem(id: number, fixedQty?: number): boolean {
        const cartItems: ICartItem[] = this.getCartItems();

        // Buscar producto y validar existencia
        const product: Product | undefined = PRODUCTS.find(
            (p: Product): boolean => p.id === id,
        );
        if (!product) return false;

        const existingItem: ICartItem | undefined = cartItems.find(
            (i: ICartItem): boolean => i.id === id,
        );

        // Si viene fixedQty, se usa esa. Si no, se incrementa la actual + 1.
        const newQty =
            fixedQty !== undefined
                ? fixedQty
                : existingItem
                  ? existingItem.qty + 1
                  : 1;

        // Validar contra el stock
        if (newQty > product.stock) {
            return false;
        }

        // Si el item ya existe, actualizamos su cantidad. Si no, lo agregamos al carrito.
        if (existingItem) {
            existingItem.qty = newQty;
        } else {
            cartItems.push({ id, qty: newQty });
        }

        localStorage.setItem("cartItems", JSON.stringify(cartItems));
        return true;
    },

    /**
     * Disminuye la cantidad de un producto en el carrito en 1 solo si la
     * cantidad actual es mayor a 1.
     * @param id El ID del producto cuya cantidad se desea disminuir en el carrito.
     */
    decreaseCartItem(id: number): void {
        const cartItems: ICartItem[] = this.getCartItems();

        const item: ICartItem | undefined = cartItems.find(
            (i: ICartItem): boolean => i.id === id,
        );

        // Cláusula de guarda: si no existe el item, no hacemos nada
        if (!item) return;

        // Solo disminuimos la cantidad si es mayor a 1 para evitar eliminar el item del carrito
        if (item.qty > 1) {
            item.qty--;
            localStorage.setItem("cartItems", JSON.stringify(cartItems));
        }
    },

    /**
     * Elimina completamente un producto del carrito, sin importar su cantidad actual.
     * @param id El ID del producto que se desea eliminar del carrito.
     */
    removeCartItem(id: number): void {
        const cartItems: ICartItem[] = this.getCartItems();
        // Creamos un nuevo array sin el producto a eliminar
        const updatedCart = cartItems.filter((i) => i.id !== id);

        localStorage.setItem("cartItems", JSON.stringify(updatedCart));
    },

    /**
     * Vacía completamente el carrito de compras eliminando la clave "cartItems" de
     * localStorage. Esto se utiliza típicamente después de completar una compra o
     * cuando el usuario desea reiniciar su carrito.
     */
    clearCart(): void {
        localStorage.removeItem("cartItems");
    },
};
