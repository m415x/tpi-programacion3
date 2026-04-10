import { defineConfig } from "vite";
import { resolve } from "path";

export default defineConfig({
    resolve: {
        alias: {
            // Definimos que "@" siempre apunte a la carpeta "/src"
            "@": resolve(__dirname, "./src"),
            "@pages": resolve(__dirname, "./src/pages"),
            "@interfaces": resolve(__dirname, "./src/types"),
            "@utils": resolve(__dirname, "./src/utils"),
        },
    },
    build: {
        rollupOptions: {
            input: {
                //d:aplicaion/dist/
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
