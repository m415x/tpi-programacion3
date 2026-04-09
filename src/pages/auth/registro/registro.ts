import type { IUser } from "../../../types/IUser";
import { navigate } from "../../../utils/navigate";
import { saveUser } from "../../../utils/localStorage";

const form = document.getElementById("form") as HTMLFormElement;
const inputEmail = document.getElementById("email") as HTMLInputElement;
const inputPassword = document.getElementById("pass") as HTMLInputElement;

form.addEventListener("submit", (e: SubmitEvent): void => {
  e.preventDefault();
  const valueEmail: string = inputEmail.value;
  const valuePassword: string = inputPassword.value;

  const user: IUser = {
    email: valueEmail,
    password: valuePassword,
    role: "client",
  };

  saveUser(user);

  alert(`Usuario ${valueEmail} registrado exitosamente`);

  navigate("/src/pages/auth/login/login.html");
});

const anchorLogin = document.querySelector(
  ".login-card__signup-signin a",
) as HTMLAnchorElement;
const URLLogin: string = "/src/pages/auth/login/login.html";

anchorLogin.addEventListener("click", (e: MouseEvent): void => {
  e.preventDefault();
  navigate(URLLogin);
});
