import { UserRole } from "@interfaces/Enums";

/**
 * Interfaz que define la estructura de un usuario en el sistema
 */
export interface IUser {
    id: string;
    isDeleted: boolean;
    createdAt: string;
    updatedAt?: string | undefined;
    version?: number | undefined;
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    password: string;
    userRole: UserRole;
}
