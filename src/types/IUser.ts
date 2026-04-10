// Interfaz que define la estructura de un usuario en la aplicación
export interface IUser {
    name: string;
    email: string;
    password: string;
    role: "client" | "admin";
}
