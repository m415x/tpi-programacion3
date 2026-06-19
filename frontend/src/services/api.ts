import axios from "axios";
import type { AxiosInstance, InternalAxiosRequestConfig } from "axios";
import { storage } from "@utils/storage";

/**
 * Crea una instancia de Axios con la configuración base para las solicitudes a la API.
 * Esto incluye la URL base del backend y los encabezados comunes para las solicitudes.
 * Esta instancia se puede importar y usar en otros servicios para realizar llamadas a la API.
 */
const api: AxiosInstance = axios.create({
    baseURL: "http://localhost:8008",
    headers: {
        "Content-Type": "application/json",
    },
});

/**
 * Inerceptor de peticiones (Request Interceptor)
 * Este bloque intercepta CUALQUIER solicitud HTTP (GET, POST, PUT, etc.)
 * antes de que llegue al servidor e inyecta los headers perimetrales.
 */
api.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        // 1. Recuperamos el usuario logueado actualmente en la sesión local
        const loggedUser = storage.getUser();

        // 2. Si hay una sesión activa, inyectamos el ID y el Email en los encabezados
        if (loggedUser) {
            config.headers["X-User-Id"] = loggedUser.id.toString();
            config.headers["X-User-Email"] = loggedUser.email;
        }

        // 3. Devolvemos la configuración modificada para que continúe la petición
        return config;
    },
    (error) => {
        // Manejo de errores en el despacho de la petición
        return Promise.reject(error);
    },
);

export default api;
