import type { UserRole } from "@interfaces/Enums";
import type { IUser } from "@interfaces/User.interface";
import api from "@services/api";
import { storage } from "@utils/storage";

/**
 * DTOs específicos para la comunicación con el backend, adaptados a la estructura que el servidor espera y
 * devuelve, manteniendo una capa de transformación limpia hacia la interfaz IUser del Frontend.
 */
interface UserResponseDTO {
    id: string;
    createdAt: string;
    isDeleted: boolean;
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    userRole: UserRole;
}

/**
 * Función de mapeo que convierte un UserResponseDTO del backend a la interfaz IUser del frontend,
 * aplicando las transformaciones necesarias para mantener la compatibilidad con el catálogo.
 * En este caso, el password no se mapea desde el backend por razones de seguridad, y se inicializa como cadena vacía.
 * @param dto Objeto recibido del backend con la estructura UserResponseDTO
 * @returns Objeto mapeado a la interfaz IUser del frontend
 */
const mapToDomain = (dto: UserResponseDTO): IUser => ({
    id: dto.id,
    isDeleted: dto.isDeleted,
    createdAt: dto.createdAt,
    firstName: dto.firstName,
    lastName: dto.lastName,
    email: dto.email,
    phone: dto.phone,
    password: "", // Protegido en el Front
    userRole: dto.userRole,
});

/**
 * Servicio para gestionar la comunicación HTTP con el backend de Spring Boot, específicamente
 * para operaciones relacionadas con usuarios, incluyendo autenticación, registro y consultas
 * por ID o email.
 */
export const userService = {
    // --- BLOQUE DE VALIDACIONES Y CRIPTOGRAFÍA ---
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
        return password.length >= 8 && /[A-Z]/.test(password) && /[0-9]/.test(password);
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
        const data: Uint8Array = encoder.encode(password);

        // Generamos el hash puro en un buffer aislado
        const buffer: ArrayBuffer = await crypto.subtle.digest("SHA-256", data as BufferSource);

        // Transformamos el buffer a un array de bytes limpio
        const bytes = new Uint8Array(buffer);

        // Convertimos el buffer a un string hexadecimal
        return Array.from(bytes)
            .map((b: number): string => b.toString(16).padStart(2, "0"))
            .join("");
    },

    // --- BLOQUE DE COMUNICACIÓN HTTP CON SPRING BOOT (API) ---
    /**
     * Valida las credenciales contra la base de datos de Spring Boot.
     * @param email Email ingresado por el usuario
     * @param passwordHash Contraseña ya encriptada en SHA-256 en el Front
     */
    async login(email: string, passwordHash: string): Promise<boolean> {
        try {
            // Buscamos al usuario en el backend por su email
            const response: { data: UserResponseDTO } = await api.post<UserResponseDTO>("/users/login", {
                email: email.trim(),
                password: passwordHash, // Enviamos el hash para que el backend lo compare con su base de datos
            });

            // Si tu backend devuelve un array, sacamos el primero
            const userAuthenticated: UserResponseDTO = response.data;

            // Le pedimos al backend que verifique las credenciales
            if (userAuthenticated) {
                // Almacenamos el usuario de dominio mapeado en el storage local de sesión ('userData')
                storage.setUser(mapToDomain(userAuthenticated));

                return true;
            }
            return false;
        } catch (error) {
            // Si el backend tira 404 o falla la conexión, el login es inválido
            return false;
        }
    },

    /**
     * Registra un nuevo usuario enviando la contraseña pre-encriptada al backend.
     * @param newUser Objeto con los datos del nuevo usuario (sin ID ni fechas)
     * @returns Promesa que resuelve con el usuario registrado
     */
    async register(
        newUser: Omit<IUser, "id" | "createdAt" | "isDeleted" | "password"> & { passwordHash: string },
    ): Promise<IUser> {
        const dto = {
            isDeleted: false, // Por defecto, el nuevo usuario no está eliminado
            firstName: newUser.firstName,
            lastName: newUser.lastName,
            email: newUser.email,
            phone: newUser.phone,
            password: newUser.passwordHash, // Viaja el string hash seguro de 64 caracteres
            userRole: newUser.userRole || "CLIENT", // Por defecto, el nuevo usuario es CLIENT
        };

        // Spring Boot se encarga de validar la unicidad del email y el autoincrement del ID
        const response: { data: UserResponseDTO } = await api.post<UserResponseDTO>("/users", dto);

        return mapToDomain(response.data);
    },

    // --- BLOQUE ADMIN ---
    /**
     * Busca un usuario por su ID de forma asíncrona
     * @param id ID del usuario a consultar
     * @return Promesa que resuelve con el usuario encontrado mapeado a la interfaz IUser del frontend, o null si no se encuentra.
     */
    async getById(id: string): Promise<IUser> {
        const response: { data: UserResponseDTO } = await api.get<UserResponseDTO>(`/users/${id}`);
        const domainUser: IUser = mapToDomain(response.data);

        console.group(`Auditoría de Usuario Registrado (ID: ${id})`);
        console.log("Datos del Objeto de Dominio:", domainUser);
        console.groupEnd();

        return domainUser;
    },

    /**
     * Busca un usuario mediante su dirección de correo electrónico (Consigna 8)
     * @param email Correo electrónico exacto a buscar
     */
    async getAll(email?: string): Promise<IUser[]> {
        const params: Record<string, string> = {};

        if (email?.trim()) params.email = email.trim();

        // Le pega al endpoint unificado que procesa Query Params en el back
        const response: { data: UserResponseDTO[] } = await api.get<UserResponseDTO[]>("/users", { params });
        const domainUsers: IUser[] = response.data.map(mapToDomain);

        // Mostrar por consola
        console.group(`Auditoría de Usuarios - Registros Encontrados: ${domainUsers.length}`);
        if (email) console.log(`Filtro por email aplicado: ${email}`);
        console.log("Lista del Dominio:", domainUsers);
        console.groupEnd();

        return domainUsers;
    },
};
