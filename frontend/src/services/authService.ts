import type { IUser } from "@interfaces/IUser";
import { storage } from "@utils/storage";

/**
 * Este servicio maneja toda la lógica relacionada con la autenticación y
 * gestión de usuarios. Incluye validación de email, fortaleza de contraseña,
 * encriptación y las funciones de login y registro.
 */
export const authService = {
    /**
     * Valida el formato del email usando una expresión regular simple.
     * @param email El email a validar.
     * @returns true si el email es válido.
     */
    validateEmail(email: string): boolean {
        // Expresión regular para validar el formato del email
        const emailRegex: RegExp = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        // Probamos el email contra la expresión regular
        return emailRegex.test(email);
    },

    /**
     * Valida la fortaleza de la contraseña. Requiere al menos 8 caracteres, una
     * letra mayúscula y un número.
     * @param password La contraseña a validar.
     * @returns true si la contraseña cumple con los requisitos de fortaleza.
     */
    validatePasswordStrength(password: string): boolean {
        return (
            password.length >= 8 &&
            /[A-Z]/.test(password) &&
            /[0-9]/.test(password)
        );
    },

    /**
     * Encripta la contraseña usando SHA-256.
     * @param password La contraseña a encriptar.
     * @returns Una promesa que resuelve con el hash hexadecimal.
     */
    async encryptPassword(password: string): Promise<string> {
        // Encriptamos la contraseña
        const encoder: TextEncoder = new TextEncoder();

        // Convertimos la contraseña a un Uint8Array para el hashing
        const data: Uint8Array<ArrayBuffer> = encoder.encode(password);

        // Generamos el hash SHA-256
        const hash: ArrayBuffer = await crypto.subtle.digest("SHA-256", data);

        // Convertimos el buffer a un string hexadecimal
        return Array.from(new Uint8Array(hash))
            .map((b: number): string => b.toString(16).padStart(2, "0"))
            .join("");
    },

    /**
     * Lógica de Login: Verifica email y contraseña, y si son correctos, guarda
     * la sesión en "userData".
     * @param email El email del usuario que intenta iniciar sesión.
     * @param passwordHash El hash de la contraseña proporcionada por el usuario.
     * @returns true si el login es exitoso, false si las credenciales son incorrectas.
     */
    login(email: string, passwordHash: string): boolean {
        // Obtenemos los usuarios registrados
        const users: IUser[] = storage.getUsers();

        // Buscamos un usuario que coincida con el email y el hash de la contraseña
        const userFound: IUser | undefined = users.find(
            (u: IUser): boolean =>
                u.email === email && u.password === passwordHash,
        );

        // Si encontramos un usuario válido, guardamos su sesión en "userData"
        // y retornamos true
        if (userFound) {
            // Creamos la sesión en "userData"
            storage.setUser(userFound);
            return true;
        }
        return false;
    },

    /**
     * Lógica de Registro: Verifica que el email no esté registrado, asigna un ID
     * autoincremental, encripta la contraseña y guarda el nuevo usuario en "users".
     * @param newUser El objeto IUser con los datos del nuevo usuario (sin ID).
     * @returns Un objeto con el resultado del registro y un mensaje descriptivo.
     */
    register(newUser: IUser): { success: boolean; message: string } {
        // Obtenemos los usuarios registrados
        const users: IUser[] = storage.getUsers();

        // Verificar existencia de email
        const emailExists: boolean = users.some(
            (user: IUser): boolean => user.email === newUser.email,
        );

        // Si el email ya existe, retornamos un mensaje de error sin registrar al usuario
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

        // Asignamos el nuevo ID
        newUser.id = lastId + 1;

        // Persistencia
        users.push(newUser);

        // Guarda la lista completa actualizada
        storage.saveUsers(users);

        return { success: true, message: "Usuario registrado con éxito." };
    },
};
