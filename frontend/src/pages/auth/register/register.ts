import { renderAuthCard } from "@utils/components";
import { initRegisterLogic } from "@pages/auth/register/register.controller";

const initRegister = () => {
    // Renderizamos la tarjeta de registro
    renderAuthCard("auth-container", true); // true = modo registro

    initRegisterLogic();
};

initRegister();
