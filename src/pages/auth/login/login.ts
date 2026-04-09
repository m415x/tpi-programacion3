import { getUsers, setSession } from "../../../utils/localStorage";
import { navigate } from "../../../utils/navigate";
import type { IUser } from "../../../types/IUser";

const form = document.getElementById("form") as HTMLFormElement;
const inputEmail = document.getElementById("email") as HTMLInputElement;
const inputPassword = document.getElementById("pass") as HTMLInputElement;

form.addEventListener("submit", (e: SubmitEvent) => {
  e.preventDefault();

  const valueEmail: string = inputEmail.value;
  const valuePassword: string = inputPassword.value;

  // Obtenemos los usuarios registrados y buscamos una coincidencia con el email y contraseña
  const users: IUser[] = getUsers();

  const userFound: IUser | undefined = users.find(
    (u: IUser) => u.email === valueEmail && u.password === valuePassword,
  );

  if (userFound) {
    // Creamos la sesión en "userData"
    setSession(userFound);

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

const anchorRegister = document.querySelector(
  ".login-card__signup-signin a",
) as HTMLAnchorElement;
const URLRegister: string = "/src/pages/auth/registro/registro.html";

anchorRegister.addEventListener("click", (e: MouseEvent) => {
  e.preventDefault();
  navigate(URLRegister);
});
