import { checkAuhtUser, logout } from "../../../utils/auth";

const buttonLogout = document.getElementById(
  "logoutButton",
) as HTMLButtonElement;

buttonLogout?.addEventListener("click", (): void => {
  logout();
});

const initPage = (): void => {
  checkAuhtUser(
    "/src/pages/auth/login/login.html",
    "/src/pages/admin/home/home.html",
    "client",
  );
};

initPage();
