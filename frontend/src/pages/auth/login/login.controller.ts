import { userService } from "@services/user.service";
import { navigate } from "@utils/navigate";
import { PATHS } from "@utils/paths";

/**
 * Inicializa la lógica del login, incluyendo validaciones, encriptación de contraseña y manejo de eventos.
 */
export const initLoginLogic = (): void => {
    const loginForm = document.querySelector<HTMLFormElement>("#form");
    if (!loginForm) return;

    // Listener para el submit del formulario de login
    loginForm.addEventListener("submit", async (e: SubmitEvent) => {
        e.preventDefault();

        const email: string = (loginForm.querySelector("#email") as HTMLInputElement).value;
        const pass: string = (loginForm.querySelector("#pass") as HTMLInputElement).value;

        // Validaciones de formato de email
        if (!userService.validateEmail(email)) {
            alert("Por favor, ingresa un email válido.");
            return;
        }

        try {
            // Encriptación SHA-256 local
            const encryptedPass: string = await userService.encryptPassword(pass);

            // Intento de Login asíncrono contra Spring Boot
            const loginSuccess: boolean = await userService.login(email, encryptedPass);

            if (loginSuccess) {
                navigate(PATHS.STORE.HOME);
            } else {
                alert("Credenciales incorrectas o usuario inexistente.");
            }
        } catch (error) {
            console.error("Error en el flujo de autenticación:", error);
            alert("Error de comunicación con el servidor. Inténtelo más tarde.");
        }
    });

    const authLink = document.querySelector<HTMLLinkElement>("#auth-switch-link");
    if (!authLink) return;

    // Listener para cambiar entre login y registro
    authLink.addEventListener("click", (e: MouseEvent) => {
        e.preventDefault();
        navigate(PATHS.AUTH.REGISTER);
    });
};
