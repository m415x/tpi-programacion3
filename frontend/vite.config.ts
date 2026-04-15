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
            "@pages": resolve(__dirname, "./src/pages"),
            "@services": resolve(__dirname, "./src/services"),
            "@interfaces": resolve(__dirname, "./src/types"),
            "@utils": resolve(__dirname, "./src/utils"),
        },
    },
    build: {
        rollupOptions: {
            input: {
                index: resolve(__dirname, "index.html"),
                login: resolve(__dirname, "src/pages/auth/login/login.html"),
                register: resolve(
                    __dirname,
                    "src/pages/auth/register/register.html",
                ),
                adminHome: resolve(__dirname, "src/pages/admin/home/home.html"),
                clientHome: resolve(
                    __dirname,
                    "src/pages/client/home/home.html",
                ),
            },
        },
    },
    base: "./",
});
