import { sessionStore } from "@utils/sessionStore";
import { navigate } from "@utils/navigate";

// Función para verificar autenticación y autorización en cada carga de página
export const checkAuth = (): void => {
    // Ruta actual del navegador
    const path: string = window.location.pathname;

    // Definir qué rutas son públicas (sin necesidad de estar autenticado)
    const isAuthPage: boolean = path.includes("/auth/");
    const isLandingPage: boolean = path === "/" || path.endsWith("index.html");

    // Obtener el rol desde el store centralizado
    const role: string | null = sessionStore.getRole();

    // 1. Si no hay sesión y no está en una página pública -> al login
    if (!sessionStore.isAuthenticated() && !isAuthPage && !isLandingPage) {
        navigate("/src/pages/auth/login/login.html");
        return;
    }

    // 2. Si hay sesión pero intenta ir al login/register -> a su home
    if (sessionStore.isAuthenticated() && isAuthPage) {
        const destination: string =
            role === "admin"
                ? "/src/pages/admin/home/home.html"
                : "/src/pages/client/home/home.html";
        navigate(destination);
        return;
    }

    // 3. Autorización por Roles: Prevenir accesos cruzados
    if (path.includes("/admin/") && role !== "admin") {
        // Si un cliente intenta entrar a /admin/ -> a su zona
        navigate("/src/pages/client/home/home.html");
        return;
    }

    if (path.includes("/client/") && role !== "client") {
        // Si un admin intenta entrar a /client/ -> a su zona
        navigate("/src/pages/admin/home/home.html");
        return;
    }
};

// Función para cerrar sesión y redirigir al login
export const logout = (): void => {
    sessionStore.clear();
    navigate("/src/pages/auth/login/login.html");
};
