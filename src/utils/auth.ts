import type { IUser } from "../types/IUser";
import type { Role } from "../types/Role";
import { getSession, removeSession } from "./localStorage";
import { navigate } from "./navigate";

// Función para verificar la autenticación y el rol del usuario
export const checkAuhtUser = (
  redirect1: string,
  redirect2: string,
  roleRequired: Role,
): void => {
  const user: IUser | null = getSession();

  // Si no hay sesión iniciada en "userData" regirige al login
  if (!user) {
    navigate(redirect1);
    return;
  }

  // Si el rol del usuario logueado no coincide con el requerido para la página
  if (user.role !== roleRequired) {
    // Redirige al login o a una zona permitida
    navigate(redirect2);
    return;
  }
};

// Función para cerrar sesión y redirigir al login
export const logout = (): void => {
  removeSession();
  navigate("/src/pages/auth/login/login.html");
};
