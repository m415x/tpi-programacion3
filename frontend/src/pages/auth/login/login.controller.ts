import { authService as as } from "@/services/authService";
import { navigate } from "@utils/navigate";
import { PATHS } from "@utils/paths";

// Lógica de Login: Validaciones, encriptación y redirección.
export const initLoginLogic = (): void => {
    const loginForm = document.querySelector("#form") as HTMLFormElement;

    loginForm?.addEventListener("submit", async (e: SubmitEvent) => {
        e.preventDefault();

        const email: string = (
            document.querySelector("#email") as HTMLInputElement
        ).value;
        const pass: string = (
            document.querySelector("#pass") as HTMLInputElement
        ).value;

        // Validaciones de formato de email
        if (!as.validateEmail(email)) {
            alert("Por favor, ingresa un email válido.");
            return;
        }

        // Encriptación
        const encryptedPass: string = await as.encryptPassword(pass);

        // Intento de Login
        if (as.login(email, encryptedPass)) {
            navigate(PATHS.STORE.HOME);
        } else {
            alert("Credenciales incorrectas.");
        }
    });

    const authLink = document.getElementById(
        "auth-switch-link",
    ) as HTMLLinkElement;

    // Listener para cambiar entre login y registro
    authLink?.addEventListener("click", (e: MouseEvent) => {
        e.preventDefault();
        navigate(PATHS.AUTH.REGISTER);
    });
};
