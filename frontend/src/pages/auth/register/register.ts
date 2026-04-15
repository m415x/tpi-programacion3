import { renderAuthCard } from "@utils/components";
import { sessionStore } from "@utils/sessionStore";
import { navigate } from "@utils/navigate";
import type { IUser } from "@interfaces/IUser";

const initRegister = () => {
    // Renderizamos la tarjeta de registro
    renderAuthCard("auth-container", true); // true = modo registro

    const form = document.getElementById("form") as HTMLFormElement;
    const inputName = document.getElementById("name") as HTMLInputElement;
    const inputEmail = document.getElementById("email") as HTMLInputElement;
    const inputPassword = document.getElementById("pass") as HTMLInputElement;
    const authLink = document.getElementById(
        "auth-switch-link",
    ) as HTMLLinkElement;

    form?.addEventListener("submit", (e: SubmitEvent): void => {
        e.preventDefault();

        // Creamos un nuevo usuario
        const valueName: string = inputName.value;
        const valueEmail: string = inputEmail.value;
        const valuePassword: string = inputPassword.value;

        const user: IUser = {
            name: valueName,
            email: valueEmail,
            password: valuePassword,
            role: "client",
        };

        // Guardamos el nuevo usuario en el "sessionStore"
        sessionStore.setUsers(user)
            ? alert(`Usuario ${valueName} registrado exitosamente`)
            : alert(`El usuario ${valueName} ya existe`);

        navigate("/src/pages/auth/login/login.html");
    });

    // Listener para cambiar entre login y registro
    authLink?.addEventListener("click", (e: MouseEvent) => {
        e.preventDefault();
        navigate("/src/pages/auth/login/login.html");
    });
};

// Ejecutar la inicialización
initRegister();
