import { initLoginLogic } from "@pages/auth/login/login.controller";
import { renderAuthCard } from "@utils/components";
import { setPageTitle } from "@utils/uiUtils";

/**
 * Función principal para inicializar la página de login.
 */
const initLogin = () => {
    // Establecemos el título de la página
    setPageTitle("Inicio de Sesión");

    // Renderizamos la tarjeta de login
    renderAuthCard("#auth-container", false); // false = modo login

    // Inicializamos la lógica de login
    initLoginLogic();
};

initLogin();
