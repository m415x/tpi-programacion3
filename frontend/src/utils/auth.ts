import { storage } from "@utils/storage";
import { navigate } from "@utils/navigate";
import { PATHS } from "@utils/paths";

// Función para verificar autenticación y autorización en cada carga de página
export const checkAuth = (): void => {
    // Ruta actual del navegador
    const path: string = window.location.pathname;

    // Definir qué rutas son públicas (sin necesidad de estar autenticado)
    const isAuthPage: boolean = path.includes("/auth/");
    const isLandingPage: boolean = path === "/" || path.endsWith("index.html");

    // Obtener el rol desde el store centralizado
    const role: string | null = storage.getRole();

    // 1. Si no hay sesión y no está en una página pública -> al login
    if (!storage.isAuthenticated() && !isAuthPage && !isLandingPage) {
        navigate(PATHS.AUTH.LOGIN);
        return;
    }

    // 2. Si hay sesión pero intenta ir al login/register -> a su home
    if (storage.isAuthenticated() && isAuthPage) {
        const destination: string =
            role === "admin" ? PATHS.ADMIN.HOME : PATHS.CLIENT.HOME;
        navigate(destination);
        return;
    }

    // 3. Autorización por Roles: Prevenir accesos cruzados
    if (path.includes("/admin/") && role !== "admin") {
        // Si un cliente intenta entrar a /admin/ -> a su zona
        navigate(PATHS.CLIENT.HOME);
        return;
    }

    if (path.includes("/client/") && role !== "client") {
        // Si un admin intenta entrar a /client/ -> a su zona
        navigate(PATHS.ADMIN.HOME);
        return;
    }
};

// Función para cerrar sesión y redirigir al login
export const logout = (): void => {
    storage.clear();
    navigate(PATHS.AUTH.LOGIN);
};
