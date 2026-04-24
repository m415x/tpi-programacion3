import { initRegisterLogic } from "@pages/auth/register/register.controller";
import { renderAuthCard } from "@utils/components";

/**
 * Función principal para inicializar la página de registro.
 */
const initRegister = () => {
    // Renderizamos la tarjeta de registro
    renderAuthCard("#auth-container", true); // true = modo registro

    // Inicializamos la lógica de registro
    initRegisterLogic();
};

initRegister();
