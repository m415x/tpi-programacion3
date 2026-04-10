import type { IUser } from "@interfaces/IUser";

// Store centralizado para gestionar el estado de la sesión
export const sessionStore = {
    // --- Funciones para gestionar usuarios ---

    // Obtener usuarios
    getUsers(): IUser[] {
        const users: string | null = localStorage.getItem("users");
        return users ? JSON.parse(users) : [];
    },

    // Guardar un nuevo usuario (registro)
    saveUser(user: IUser): void {
        const users: IUser[] = this.getUsers();
        const existingUser: IUser | undefined = users.find(
            (u: IUser) => u.email === user.email,
        );

        if (existingUser) {
            alert(`El usuario ${user.email} ya existe`);
            return;
        }

        users.push(user);
        localStorage.setItem("users", JSON.stringify(users));
    },

    // --- Funciones para gestionar la sesión actual ---

    // Obtener el usuario actual
    getUser(): IUser | null {
        const data: string | null = localStorage.getItem("userData");
        return data ? JSON.parse(data) : null;
    },

    // Guardar sesión
    setUser(user: IUser): void {
        localStorage.setItem("userData", JSON.stringify(user));
    },

    // Eliminar sesión
    clear(): void {
        localStorage.removeItem("userData");
    },

    // Verificar si hay una sesión activa
    isAuthenticated(): boolean {
        return this.getUser() !== null;
    },

    // Obtener el rol rápidamente
    getRole(): string | null {
        const user: IUser | null = this.getUser();
        return user ? user.role : null;
    },
};
