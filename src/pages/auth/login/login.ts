import { renderAuthCard } from "@utils/components";
import { sessionStore } from "@utils/sessionStore";
import { navigate } from "@utils/navigate";
import type { IUser } from "@interfaces/IUser";

// Renderizamos la tarjeta de login
renderAuthCard("auth-container", false); // false = modo login

const form = document.getElementById("form") as HTMLFormElement;
const inputEmail = document.getElementById("email") as HTMLInputElement;
const inputPassword = document.getElementById("pass") as HTMLInputElement;

form.addEventListener("submit", (e: SubmitEvent) => {
    e.preventDefault();

    const valueEmail: string = inputEmail.value;
    const valuePassword: string = inputPassword.value;

    // Obtenemos los usuarios registrados y buscamos una coincidencia con el email y contraseña
    const users: IUser[] = sessionStore.getUsers();

    const userFound: IUser | undefined = users.find(
        (u: IUser) => u.email === valueEmail && u.password === valuePassword,
    );

    if (userFound) {
        // Creamos la sesión en "userData"
        sessionStore.setUser(userFound);

        // Redirección basado en el rol
        const path: string =
            userFound.role === "admin"
                ? "/src/pages/admin/home/home.html"
                : "/src/pages/store/home/home.html";
        navigate(path);
    } else {
        alert("Usuario o contraseña incorrectos");
    }
});

// Listener para cambiar entre login y registro
document
    .getElementById("auth-switch-link")
    ?.addEventListener("click", (e: MouseEvent) => {
        e.preventDefault();
        navigate("/src/pages/auth/login/login.html");
    });
