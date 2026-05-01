import { authService } from "@services/authService";
import { navigate } from "@utils/navigate";
import { PATHS } from "@utils/paths";

/**
 * Inicializa la lógica del login, incluyendo validaciones, encriptación de contraseña y manejo de eventos.
 */
export const initLoginLogic = (): void => {
    const loginForm = document.querySelector<HTMLFormElement>("#form");

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!loginForm) return;

    // Listener para el submit del formulario de login
    loginForm.addEventListener("submit", async (e: SubmitEvent) => {
        e.preventDefault();

        const email: string = (
            document.querySelector("#email") as HTMLInputElement
        ).value;
        const pass: string = (
            document.querySelector("#pass") as HTMLInputElement
        ).value;

        // Validaciones de formato de email
        if (!authService.validateEmail(email)) {
            alert("Por favor, ingresa un email válido.");
            return;
        }

        // Encriptación
        const encryptedPass: string = await authService.encryptPassword(pass);

        // Intento de Login
        if (authService.login(email, encryptedPass)) {
            navigate(PATHS.STORE.HOME);
        } else {
            alert("Credenciales incorrectas.");
        }
    });

    const authLink =
        document.querySelector<HTMLLinkElement>("#auth-switch-link");

    //Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!authLink) return;

    // Listener para cambiar entre login y registro
    authLink.addEventListener("click", (e: MouseEvent) => {
        e.preventDefault();
        navigate(PATHS.AUTH.REGISTER);
    });
};
