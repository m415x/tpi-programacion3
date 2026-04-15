import type { IUser } from "@/types/IUser";
import { storage } from "@utils/storage";

export const authService = {
    // Valida si un email tiene un formato correcto.
    validateEmail(email: string): boolean {
        const emailRegex: RegExp = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        return emailRegex.test(email);
    },

    // Valida la fortaleza de la contraseña.
    // Mínimo 8 caracteres, una mayúscula y un número.
    validatePasswordStrength(password: string): boolean {
        return (
            password.length >= 8 &&
            /[A-Z]/.test(password) &&
            /[0-9]/.test(password)
        );
    },

    // Encripta la contraseña usando SHA-256.
    async encryptPassword(password: string): Promise<string> {
        const encoder: TextEncoder = new TextEncoder();
        const data: Uint8Array<ArrayBuffer> = encoder.encode(password);
        const hash: ArrayBuffer = await crypto.subtle.digest("SHA-256", data);

        // Convertimos el buffer a un string hexadecimal
        return Array.from(new Uint8Array(hash))
            .map((b: number): string => b.toString(16).padStart(2, "0"))
            .join("");
    },

    // Lógica de Login: Verifica credenciales contra el storage.
    login(email: string, passwordHash: string): boolean {
        // Obtenemos los usuarios registrados y buscamos una coincidencia con el email y contraseña
        const users: IUser[] = storage.getUsers();

        const userFound: IUser | undefined = users.find(
            (u: IUser): boolean =>
                u.email === email && u.password === passwordHash,
        );

        if (userFound) {
            // Creamos la sesión en "userData"
            storage.setUser(userFound);
            return true;
        }
        return false;
    },

    // Lógica de Registro: Valida unicidad y guarda en storage
    register(newUser: IUser): { success: boolean; message: string } {
        // Obtenemos los usuarios registrados
        const users: IUser[] = storage.getUsers();

        // Verificar existencia de email
        const emailExists: boolean = users.some(
            (user: IUser): boolean => user.email === newUser.email,
        );

        if (emailExists) {
            return {
                success: false,
                message: "El email ya se encuentra registrado.",
            };
        }

        // Asignación de ID autoincremental
        // Buscamos el ID más alto y le sumamos 1. Si no hay usuarios, empezamos en 1.
        const lastId: number =
            users.length > 0
                ? Math.max(...users.map((user: IUser): number => user.id))
                : 0;

        newUser.id = lastId + 1;

        // Persistencia
        users.push(newUser);
        storage.saveUsers(users); // Guarda la lista completa actualizada

        return { success: true, message: "Usuario registrado con éxito." };
    },
};
