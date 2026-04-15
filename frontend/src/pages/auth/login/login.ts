import { renderAuthCard } from "@utils/components";
import { initLoginLogic } from "@pages/auth/login/login.controller";

const initLogin = () => {
    // Renderizamos la tarjeta de login
    renderAuthCard("auth-container", false); // false = modo login

    initLoginLogic();
};

initLogin();
