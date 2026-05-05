/**
 * Punto de entrada principal de la aplicación frontend. Iniciar la aplicación y
 * realizar cualquier configuración inicial necesaria.
 */
import { checkAuth } from "@utils/authGuard";
import { initFavicon } from "@utils/components";

// Inicializar el favicon de la aplicación
initFavicon();

// Verificar si el usuario ya está autenticado para redirigirlo a la página
checkAuth();
