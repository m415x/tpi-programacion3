import { getSession } from "./utils/localStorage";
import { navigate } from "./utils/navigate";
import type { IUser } from "./types/IUser";

const checkAuth = (): void => {
  // Obtenemos la sesión actual de "userData"
  const user: IUser | null = getSession();

  // Ruta actual en la que se encuentra el usuario
  const path: string = window.location.pathname;

  // Si intenta acceder a páginas de ADMIN
  if (path.includes("/admin/") && (!user || user.role !== "admin")) {
    navigate("/src/pages/auth/login/login.html");
  }

  // Si intenta acceder a páginas de CLIENT
  if (path.includes("/client/") && (!user || user.role !== "client")) {
    navigate("/src/pages/auth/login/login.html");
  }
};

// Se ejecuta cada vez que el archivo se carga en el navegador
checkAuth();
