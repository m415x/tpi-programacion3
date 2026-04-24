import { Role } from "@interfaces/Role";

/**
 * Interfaz que define la estructura de un usuario en el sistema
 */
export interface IUser {
    id: number;
    deleted: boolean;
    createdAt: string;
    name: string;
    email: string;
    password: string;
    role: Role;
}
