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

    // --- BLOQUE DE COMUNICACIÓN HTTP CON SPRING BOOT (API) ---
    /**
     * Valida las credenciales contra la base de datos de Spring Boot.
     * @param email Email ingresado por el usuario
     * @param rawPassword Contraseña en texto plano ingresada en el input
     */
    async login(email: string, rawPassword: string): Promise<boolean> {
        try {
            // Buscamos al usuario en el backend por su email
            const response: { data: UserResponseDTO } = await api.post<UserResponseDTO>("/users/login", {
                email: email.trim(),
                password: rawPassword, // Enviamos el hash para que el backend lo compare con su base de datos
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
        newUser: Omit<IUser, "id" | "createdAt" | "isDeleted" | "password"> & { rawPassword: string },
    ): Promise<IUser> {
        const dto = {
            isDeleted: false, // Por defecto, el nuevo usuario no está eliminado
            firstName: newUser.firstName,
            lastName: newUser.lastName,
            email: newUser.email,
            phone: newUser.phone,
            password: newUser.rawPassword, // Viaja el texto plano de forma directa
            userRole: newUser.userRole || "CLIENT", // Por defecto, el nuevo usuario es CLIENT
        };

        // Spring Boot se encarga de validar la unicidad del email y el autoincrement del ID
        const response: { data: UserResponseDTO } = await api.post<UserResponseDTO>("/users", dto);

        return mapToDomain(response.data);
    },

    /**
     * Recupera el perfil del usuario actualmente logueado, la API deduce el usuario por las cabeceras perimetrales.
     */
    async getProfile(): Promise<IUser> {
        const response = await api.get<UserResponseDTO>("/users/profile");

        return mapToDomain(response.data);
    },

    /**
     * Actualiza de forma parcial los datos del perfil del usuario logueado. Se envía mediante PATCH a /users/profile
     * sin exponer IDs en la URL.
     */
    async updateProfile(
        profileChanges: Partial<Omit<IUser, "id" | "createdAt" | "isDeleted" | "userRole">>
    ): Promise<IUser> {
        const response = await api.patch<UserResponseDTO>("/users/profile", profileChanges);

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

    /**
     * Recupera el usuario con mayor cantidad de pedidos en el sistema.
     * Conecta directamente con la consulta nativa optimizada del backend.
     * @returns Promesa con el objeto del usuario estrella mapeado al dominio.
     */
    async getTopCustomer(): Promise<IUser> {
        // Recorremos el storage local para extraer las credenciales activas del Administrador logueado
        const currentUser = storage.getUser();

        // Configuramos los headers perimetrales requeridos de forma manual para evitar el filtro 400
        const config = {
            headers: {
                "X-User-Id": currentUser?.id || "",
                "X-User-Email": currentUser?.email || "",
            },
        };

        const response = await api.get<UserResponseDTO>("/users/top-customer", config);

        return mapToDomain(response.data);
    },

    /**
     * Aplica una actualización parcial a un usuario en el backend (PATCH).
     * @param id ID del usuario a modificar.
     * @param changes Objeto parcial con las modificaciones del perfil.
     */
    async partialUpdate(id: string, changes: Partial<IUser>): Promise<IUser> {
        const dto = {
            firstName: changes.firstName,
            lastName: changes.lastName,
            phone: changes.phone,
            userRole: changes.userRole,
        };

        const response = await api.patch<UserResponseDTO>(`/users/${id}`, dto);
        return mapToDomain(response.data);
    },
};
