/**
 * Este objeto centraliza todas las rutas de navegación de la aplicación.
 * Proporciona una estructura organizada para acceder a las URLs de la tienda,
 * autenticación y perfiles de usuario, facilitando el mantenimiento y
 * evitando el uso de strings literales dispersos por el código.
 */
export const PATHS = {
    STORE: {
        HOME: "/src/pages/store/home/home.html",
        CART: "/src/pages/store/cart/cart.html",
        DETAIL: (id: number) =>
            `/src/pages/store/productDetail/productDetail.html?id=${id}`,
    },
    AUTH: {
        LOGIN: "/src/pages/auth/login/login.html",
        REGISTER: "/src/pages/auth/register/register.html",
    },
    CLIENT: {
        HOME: "/src/pages/client/home/home.html",
    },
    ADMIN: {
        HOME: "/src/pages/admin/home/home.html",
    },
} as const;
