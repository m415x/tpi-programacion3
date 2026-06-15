import { UserRole } from "@/interfaces/Enums";

/**
 * Interfaz que define la estructura de un usuario en el sistema
 */
export interface IUser {
    id: number;
    deleted: boolean;
    createdAt: string;
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    password: string;
    userRole: UserRole;
}
