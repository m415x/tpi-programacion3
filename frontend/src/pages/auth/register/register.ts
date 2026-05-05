import { initRegisterLogic } from "@pages/auth/register/register.controller";
import { renderAuthCard } from "@utils/components";
import { setPageTitle } from "@utils/uiUtils";

/**
 * Función principal para inicializar la página de registro.
 */
const initRegister = () => {
    // Establecemos el título de la página
    setPageTitle("Registro");

    // Renderizamos la tarjeta de registro
    renderAuthCard("#auth-container", true); // true = modo registro

    // Inicializamos la lógica de registro
    initRegisterLogic();
};

initRegister();
