export const PATHS = {
    STORE: {
        HOME: "/src/pages/store/home/home.html",
        CART: "/src/pages/store/cart/cart.html",
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
} as const; // 'as const' hace que los valores sean de solo lectura y más precisos para TS
