import axios from "axios";
import type { AxiosInstance } from "axios";

/**
 * Crear una instancia de Axios con la configuración base para las solicitudes a la API.
 * Esto incluye la URL base del backend y los encabezados comunes para las solicitudes.
 * Esta instancia se puede importar y usar en otros servicios para realizar llamadas a la API.
 */
const api: AxiosInstance = axios.create({
    baseURL: "http://localhost:8008",
    headers: {
        "Content-Type": "application/json",
    },
});

export default api;
