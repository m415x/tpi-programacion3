import type { IUser } from "../types/IUser";

export const getUsers = (): IUser[] => {
  const users: string | null = localStorage.getItem("users");
  return users ? JSON.parse(users) : [];
};

export const saveUser = (user: IUser): void => {
  const users: IUser[] = getUsers();

  // verificar que no exista el usuario
  const existingUser: IUser | undefined = users.find(
    (u: IUser) => u.email === user.email,
  );

  if (existingUser) {
    alert(`El usuario ${user.email} ya existe`);
    return;
  }
  users.push(user);
  localStorage.setItem("users", JSON.stringify(users));
};

export const setSession = (user: IUser): void => {
  localStorage.setItem("userData", JSON.stringify(user));
};

export const getSession = (): IUser | null => {
  const session: string | null = localStorage.getItem("userData");
  return session ? JSON.parse(session) : null;
};

export const removeSession = (): void => {
  localStorage.removeItem("userData");
};
