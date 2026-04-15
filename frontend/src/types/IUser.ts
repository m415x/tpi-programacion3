// Interfaz que define la estructura de un usuario en la aplicación
export interface IUser {
    id: number;
    deleted: boolean;
    createdAt: string;
    name: string;
    email: string;
    password: string;
    role: "client" | "admin";
}
