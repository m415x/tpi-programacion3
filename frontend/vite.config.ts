import { defineConfig } from "vite";
import { resolve } from "path";
import { fileURLToPath } from "url";
import { dirname } from "path";

// Simular __dirname en ES Modules para evitar errores de TypeScript
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

export default defineConfig({
    resolve: {
        alias: {
            "@": resolve(__dirname, "./src"),
            "@interfaces": resolve(__dirname, "./src/interfaces"),
            "@pages": resolve(__dirname, "./src/pages"),
            "@services": resolve(__dirname, "./src/services"),
            "@utils": resolve(__dirname, "./src/utils"),
        },
    },
    build: {
        rollupOptions: {
            input: {
                index: resolve(__dirname, "index.html"),
                // Auth pages
                authLogin: resolve(__dirname, "src/pages/auth/login/login.html"),
                authRegister: resolve(__dirname, "src/pages/auth/register/register.html"),
                // Store pages
                storeHome: resolve(__dirname, "src/pages/store/home/home.html"),
                storeCart: resolve(__dirname, "src/pages/store/cart/cart.html"),
                storeProductDetail: resolve(__dirname, "src/pages/store/productDetail/productDetail.html"),
                // Admin pages
                adminHome: resolve(__dirname, "src/pages/admin/home/home.html"),
                adminProducts: resolve(__dirname, "src/pages/admin/products/products.html"),
                // Client pages
                clientHome: resolve(__dirname, "src/pages/client/home/home.html"),
            },
        },
    },
    base: "./",
});
