import { initLoginLogic } from "@pages/auth/login/login.controller";
import { renderAuthCard } from "@utils/components";

/**
 * Función principal para inicializar la página de login.
 */
const initLogin = () => {
    // Renderizamos la tarjeta de login
    renderAuthCard("#auth-container", false); // false = modo login

    // Inicializamos la lógica de login
    initLoginLogic();
};

initLogin();
