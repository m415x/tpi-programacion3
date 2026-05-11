import { authService } from "@services/authService";
import type { IUser } from "@interfaces/IUser";
import { Role } from "@interfaces/Role";
import { PATHS } from "@utils/paths";
import { navigate } from "@utils/navigate";

/**
 * Inicializa la lógica del formulario de registro, incluyendo validaciones, encriptación de contraseña y manejo de eventos.
 */
export const initRegisterLogic = (): void => {
    const registerForm = document.querySelector<HTMLFormElement>("#form");
    if (!registerForm) return;

    // Listener para el submit del formulario de registro
    registerForm.addEventListener("submit", async (e: SubmitEvent) => {
        e.preventDefault();

        const name: string = (
            registerForm.querySelector("#name") as HTMLInputElement
        ).value;
        const email: string = (
            registerForm.querySelector("#email") as HTMLInputElement
        ).value;
        const pass: string = (
            registerForm.querySelector("#pass") as HTMLInputElement
        ).value;

        // Validaciones de formato de email
        if (!authService.validateEmail(email)) {
            alert("Por favor, ingresa un email válido.");
            return;
        }

        // Validaciones de contraseña
        if (!authService.validatePasswordStrength(pass)) {
            alert(
                "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número.",
            );
            return;
        }

        // Encriptación
        const encryptedPass: string = await authService.encryptPassword(pass);

        // Creación el objeto (el ID lo asignará el Auth Service)
        const newUser: IUser = {
            id: 0, // Valor temporal
            deleted: false,
            createdAt: new Date().toISOString(),
            name: name,
            email: email,
            password: encryptedPass,
            role: Role.CLIENT,
        };

        // Intento de Registro
        const result: { success: boolean; message: string } =
            authService.register(newUser);

        if (result.success) {
            alert(result.message);
            navigate(PATHS.AUTH.LOGIN);
        } else {
            // Si el email ya existe, el mensaje vendrá del service
            alert(result.message);
        }
    });

    const authLink =
        document.querySelector<HTMLLinkElement>("#auth-switch-link");
    if (!authLink) return;

    // Listener para cambiar entre login y registro
    authLink.addEventListener("click", (e: MouseEvent) => {
        e.preventDefault();
        navigate(PATHS.AUTH.LOGIN);
    });
};
