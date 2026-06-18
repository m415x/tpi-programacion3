import type { ICategory } from "@interfaces/Category.interface";
import api from "@services/api";

/**
 * DTOs específicos para la comunicación con el backend, adaptados a la estructura que el servidor espera y
 * devuelve, manteniendo una capa de transformación limpia hacia la interfaz ICategory del Frontend.
 */
interface CategoryResponseDTO {
    id: number;
    deleted: boolean;
    createdAt: string;
    name: string;
    description: string;
}

interface CategoryRequestDTO {
    name: string;
    description: string;
}

/**
 * Función de mapeo que convierte un CategoryResponseDTO del backend a la interfaz ICategory del frontend,
 * aplicando las transformaciones necesarias para mantener la compatibilidad con el catálogo.
 * @param dto Objeto recibido del backend con la estructura CategoryResponseDTO
 * @returns Objeto mapeado a la interfaz ICategory del frontend
 */
const mapToDomain = (dto: CategoryResponseDTO): ICategory => ({
    id: dto.id,
    isDeleted: dto.deleted,
    createdAt: dto.createdAt,
    name: dto.name,
    description: dto.description,
});

/**
 * Servicio para gestionar la comunicación HTTP con el backend de Spring Boot
 * para el recurso de Categorías, manteniendo compatibilidad con filtros del Front
 * e imágenes dinámicas persistentes.
 */
export const categoryService = {
    /**
     * Trae todos los categorías desde el backend. Si recibe un término de búsqueda,
     * utiliza el filtro por Query Param optimizado en base de datos.
     * @param name Término opcional para filtrar por nombre en el servidor
     * @returns Promesa con la lista de categorías procesados por el backend
     */
    async getAll(name?: string): Promise<ICategory[]> {
        const response = await api.get<CategoryResponseDTO[]>("/categories", {
            params: name && name.trim() !== "" ? { name } : {},
        });

        // Convertimos todo el array de DTOs en tu interfaz limpia ICategory[]
        return response.data.map(mapToDomain);
    },

    /**
     * Recupera un único categoría de la base de datos dado su ID.
     * @param id ID del categoría
     */
    async getById(id: number): Promise<ICategory> {
        const response = await api.get<CategoryResponseDTO>(`/categories/${id}`);

        return mapToDomain(response.data);
    },

    /**
     * Crea una nueva categoría en el backend a partir de los datos proporcionados.
     * @param category Objeto con los datos del categoría a crear, excluyendo el ID.
     * @returns Promesa con la categoría creada, mapeada a la interfaz ICategory del frontend.
     */
    async create(category: Omit<ICategory, "id">): Promise<ICategory> {
        const dto: CategoryRequestDTO = {
            name: category.name,
            description: category.description,
        };

        const response = await api.post<CategoryResponseDTO>("/categories", dto);

        return mapToDomain(response.data);
    },

    /**
     * Reemplaza por completo una categoría existente en el backend (PUT).
     * Requiere que se envíen todos los campos obligatorios del contrato del servidor.
     * @param category Objeto categoría completo del dominio, incluyendo el ID de la categoría a modificar.
     * @returns Promesa con la categoría actualizada, mapeada a la interfaz ICategory del frontend.
     */
    async update(category: ICategory): Promise<ICategory> {
        const dto: CategoryRequestDTO = {
            name: category.name,
            description: category.description,
        };

        const response = await api.put<CategoryResponseDTO>(`/categories/${category.id}`, dto);

        return mapToDomain(response.data);
    },

    /**
     * Aplica una actualización parcial a una categoría en el backend (PATCH).
     * Permite enviar únicamente las propiedades modificadas a la API.
     * @param id ID de la categoría a modificar.
     * @param changes Objeto parcial con las propiedades del dominio que se desean alterar.
     * @returns Promesa con la categoría actualizada, mapeada a la interfaz ICategory del frontend.
     */
    async partialUpdate(id: number, changes: Partial<ICategory>): Promise<ICategory> {
        // Inicializamos un objeto de payload vacío
        const dto: Partial<CategoryRequestDTO> = {};

        if (changes.name !== undefined) dto.name = changes.name;
        if (changes.description !== undefined) dto.description = changes.description;

        const response = await api.patch<CategoryResponseDTO>(`/categories/${id}`, dto);

        return mapToDomain(response.data);
    },

    /**
     * Elimina una categoría del backend dado su ID.
     * @param id ID de la categoría a eliminar.
     * @returns Promesa que se resuelve cuando la eliminación se ha completado exitosamente.
     */
    async delete(id: number): Promise<void> {
        await api.delete(`/categories/${id}`);
    },
};
