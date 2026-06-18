import { UserRole } from "@interfaces/Enums";
import type { IUser } from "@interfaces/User.interface";
import { userService } from "@services/user.service";
import { navigate } from "@utils/navigate";
import { PATHS } from "@utils/paths";

/**
 * Inicializa la lógica del formulario de registro, incluyendo validaciones, encriptación de contraseña y manejo de eventos.
 */
export const initRegisterLogic = (): void => {
    const registerForm = document.querySelector<HTMLFormElement>("#form");
    if (!registerForm) return;

    // Listener para el submit del formulario de registro
    registerForm.addEventListener("submit", async (e: SubmitEvent): Promise<void> => {
        e.preventDefault();

        const firstName: string = (registerForm.querySelector("#firstName") as HTMLInputElement).value.trim();
        const lastName: string = (registerForm.querySelector("#lastName") as HTMLInputElement).value.trim();
        const email: string = (registerForm.querySelector("#email") as HTMLInputElement).value.trim();
        const phone: string = (registerForm.querySelector("#phone") as HTMLInputElement)?.value || "0000000000";
        const pass: string = (registerForm.querySelector("#pass") as HTMLInputElement).value;

        // Validaciones de formato de email
        if (!userService.validateEmail(email)) {
            alert("Por favor, ingresa un email válido.");
            return;
        }

        // Validaciones de contraseña
        if (!userService.validatePasswordStrength(pass)) {
            alert("La contraseña debe tener al menos 8 caracteres, una mayúscula y un número.");
            return;
        }

        try {
            const encryptedPass: string = await userService.encryptPassword(pass);

            // Armamos el payload omitiendo campos generados por el backend, pasando la contraseña hash
            const registerData = {
                firstName,
                lastName,
                email,
                phone,
                passwordHash: encryptedPass,
                userRole: UserRole.CLIENT,
            };

            // Intentamos registrar en la base de datos de Spring Boot
            await userService.register(registerData);

            alert("Usuario registrado con éxito.");
            navigate(PATHS.AUTH.LOGIN);
        } catch (error: any) {
            console.error("Error al registrar usuario:", error);

            // Si el backend responde con un error controlado (ej: Email duplicado que tire HTTP 400 o 409)
            if (error.response && error.response.data && error.response.data.message) {
                alert(error.response.data.message);
            } else {
                alert("No se pudo completar el registro. Verifique su conexión con el servidor.");
            }
        }
    });

    const authLink = document.querySelector<HTMLLinkElement>("#auth-switch-link");
    if (!authLink) return;

    // Listener para cambiar entre login y registro
    authLink.addEventListener("click", (e: MouseEvent) => {
        e.preventDefault();
        navigate(PATHS.AUTH.LOGIN);
    });
};
