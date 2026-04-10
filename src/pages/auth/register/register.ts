import { renderAuthCard } from "@utils/components";
import { sessionStore } from "@utils/sessionStore";
import { navigate } from "@utils/navigate";
import type { IUser } from "@interfaces/IUser";

// Renderizamos la tarjeta de registro
renderAuthCard("auth-container", true); // true = modo registro

const form = document.getElementById("form") as HTMLFormElement;
const inputName = document.getElementById("name") as HTMLInputElement;
const inputEmail = document.getElementById("email") as HTMLInputElement;
const inputPassword = document.getElementById("pass") as HTMLInputElement;

form.addEventListener("submit", (e: SubmitEvent): void => {
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
    sessionStore.saveUser(user);
    alert(`Usuario ${valueEmail} registrado exitosamente`);

    navigate("/src/pages/auth/login/login.html");
});

// Listener para cambiar entre login y registro
document
    .getElementById("auth-switch-link")
    ?.addEventListener("click", (e: MouseEvent) => {
        e.preventDefault();
        navigate("/src/pages/auth/register/register.html");
    });
