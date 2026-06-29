import { UserRole } from "@interfaces/Enums";
import { userService } from "@services/user.service";
import { navigateClear } from "@utils/navigate";
import { PATHS } from "@utils/paths";
import { storage } from "@utils/storage";

/**
 * Función para verificar la autenticación y autorización del usuario en cada
 * carga de página. Redirige al usuario si no tiene permisos para acceder a la
 * ruta actual.
 */
export const checkAuth = async (): Promise<void> => {
    // Ruta actual del navegador
    const path: string = window.location.pathname;

    // Determinar si la ruta es de autenticación o la página de inicio
    const isAuthPage: boolean = path.includes("/auth/");
    const isLandingPage: boolean = path === "/" || path.endsWith("index.html");

    if (storage.isAuthenticated()) {
        try {
            // Rehidratamos y validamos la sesión actual contra el nuevo endpoint seguro /profile
            storage.setUser(await userService.getProfile());
        } catch (error) {
            console.error("Sesión inválida o cuenta inexistente en el servidor.");
            storage.clear(); // Deslogueo automático si fue borrado lógicamente
            navigateClear(PATHS.AUTH.LOGIN);
            return;
        }
    }

    // Obtener el rol del usuario (si está autenticado)
    const role: UserRole | null = storage.getRole();

    // 1. Si no está autenticado y no está en una página de auth/ o landing -> al login
    if (!storage.isAuthenticated() && !isAuthPage && !isLandingPage) {
        navigateClear(PATHS.AUTH.LOGIN);
        return;
    }

    // 2. Si está autenticado y está en una página de auth/ o landing -> a su zona
    if (storage.isAuthenticated() && isAuthPage) {
        const destination: string = role === UserRole.ADMIN ? PATHS.ADMIN.HOME : PATHS.STORE.HOME;
        navigateClear(destination);
        return;
    }

    // 3. Verificar permisos según el rol y la ruta
    if (path.includes("/admin/") && role !== UserRole.ADMIN) {
        // Si un cliente intenta entrar a /admin/ -> a su zona
        navigateClear(PATHS.CLIENT.PROFILE);
        return;
    }
    if (path.includes("/client/") && role !== UserRole.CLIENT) {
        // Si un admin intenta entrar a /client/ -> a su zona
        navigateClear(PATHS.ADMIN.HOME);
        return;
    }
};

/**
 * Función para cerrar sesión del usuario. Limpia el almacenamiento y redirige
 * al login.
 */
export const logout = (): void => {
    if (confirm("¿Estás seguro que deseas cerrar sesión?")) {
        storage.clear();
        navigateClear(PATHS.AUTH.LOGIN);
    }
};
