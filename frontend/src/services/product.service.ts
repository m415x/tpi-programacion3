import type { IProduct } from "@interfaces/Product.interface";
import api from "@services/api";

/**
 * DTOs específicos para la comunicación con el backend, adaptados a la estructura que el servidor espera y
 * devuelve, manteniendo una capa de transformación limpia hacia la interfaz IProduct del Frontend.
 */
interface ProductResponseDTO {
    id: string;
    deleted: boolean;
    createdAt: string;
    name: string;
    price: number;
    description: string;
    stock: number;
    image: string;
    available: boolean;
    categoryId: string | null;
}

interface ProductRequestDTO {
    name: string;
    price: number;
    description: string;
    stock: number;
    image: string;
    available: boolean;
    categoryId: string;
}

/**
 * Estructura opcional para los filtros de búsqueda que soporta el backend
 */
export interface ProductFilterCriteria {
    name?: string;
    available?: boolean;
    lowStock?: number;
}

/**
 * Función de mapeo que convierte un ProductResponseDTO del backend a la interfaz IProduct del frontend,
 * aplicando las transformaciones necesarias para mantener la compatibilidad con el catálogo.
 * @param dto Objeto recibido del backend con la estructura ProductResponseDTO
 * @returns Objeto mapeado a la interfaz IProduct del frontend
 */
const mapToDomain = (dto: ProductResponseDTO): IProduct => ({
    id: dto.id,
    isDeleted: dto.deleted,
    createdAt: dto.createdAt,
    name: dto.name,
    price: dto.price,
    description: dto.description,
    stock: dto.stock,
    imageUrl: dto.image ? `/img/products/${dto.image}` : "/img/products/default-food.jpg",
    isAvailable: dto.available,
    categoryId: dto.categoryId,
});

/**
 * Servicio para gestionar la comunicación HTTP con el backend de Spring Boot
 * para el recurso de Productos, manteniendo compatibilidad con filtros del Front
 * e imágenes dinámicas persistentes.
 */
export const productService = {
    /**
     * Trae todos los productos desde el backend. Si recibe un término de búsqueda,
     * utiliza el filtro por Query Param optimizado en base de datos.
     * @param criteria Objeto opcional con los filtros (name, available, lowStock)
     * @returns Promesa con la lista de productos procesados por el backend
     */
    async getAll(criteria?: ProductFilterCriteria): Promise<IProduct[]> {
        // Inicializamos un objeto de parámetros limpio
        const queryParams: Record<string, any> = {};

        // Mapeo quirúrgico basado en las reglas de tu backend
        if (criteria) {
            if (criteria.name && criteria.name.trim() !== "") {
                queryParams.name = criteria.name.trim();
            }

            if (criteria.available !== undefined && criteria.available !== null) {
                queryParams.available = criteria.available;
            }

            if (criteria.lowStock !== undefined && criteria.lowStock > 0) {
                queryParams.lowStock = criteria.lowStock;
            }
        }

        // Realizamos la petición enviando solo los Query Params activos
        const response = await api.get<ProductResponseDTO[]>("/products", {
            params: queryParams,
        });

        return response.data.map(mapToDomain);
    },

    /**
     * Recupera un único producto de la base de datos dado su ID.
     * @param id ID del producto
     */
    async getById(id: string): Promise<IProduct> {
        const response = await api.get<ProductResponseDTO>(`/products/${id}`);

        return mapToDomain(response.data);
    },

    /**
     * Crea un nuevo producto en el backend a partir de los datos proporcionados, incluyendo el nombre del archivo de imagen.
     * @param product Objeto con los datos del producto a crear, excluyendo el ID y la URL de la imagen, pero incluyendo
     * el nombre del archivo de imagen.
     * @param Objeto producto completo del dominio.
     * @param imageFileName Nombre del archivo de imagen actual.
     * @returns Promesa con el producto creado, mapeado a la interfaz IProduct del frontend.
     */
    async create(product: Omit<IProduct, "id" | "imageUrl"> & { imageFileName: string }): Promise<IProduct> {
        const dto: ProductRequestDTO = {
            name: product.name,
            price: product.price,
            description: product.description,
            stock: product.stock,
            image: product.imageFileName,
            available: product.isAvailable,
            categoryId: product.categoryId || "",
        };

        const response = await api.post<ProductResponseDTO>("/products", dto);

        return mapToDomain(response.data);
    },

    /**
     * Reemplaza por completo un producto existente en el backend (PUT).
     * Requiere que se envíen todos los campos obligatorios del contrato del servidor.
     * @param product Objeto producto completo del dominio con su ID respectivo.
     * @param imageFileName Nombre del archivo de imagen actual o modificado.
     * @returns Promesa con el producto actualizado mapeado al dominio del Front.
     */
    async update(product: IProduct & { imageFileName: string }): Promise<IProduct> {
        const dto: ProductRequestDTO = {
            name: product.name,
            price: product.price,
            description: product.description,
            stock: product.stock,
            image: product.imageFileName,
            available: product.isAvailable || product.stock > 0, // Si el producto tiene stock, lo consideramos disponible
            categoryId: product.categoryId || "",
        };

        const response = await api.put<ProductResponseDTO>(`/products/${product.id}`, dto);

        return mapToDomain(response.data);
    },

    /**
     * Aplica una actualización parcial a un producto en el backend (PATCH).
     * Permite enviar únicamente las propiedades modificadas a la API.
     * @param id ID del producto a modificar.
     * @param changes Objeto parcial con las propiedades del dominio que se desean alterar.
     * @returns Promesa con el producto modificado mapeado al dominio del Front.
     */
    async partialUpdate(id: string, changes: Partial<IProduct & { imageFileName: string }>): Promise<IProduct> {
        // Inicializamos un objeto de payload vacío
        const dto: Partial<ProductRequestDTO> = {};

        // Mapeamos quirúrgicamente al DTO de Java solo las propiedades que el Front envía
        if (changes.name !== undefined) dto.name = changes.name;
        if (changes.price !== undefined) dto.price = changes.price;
        if (changes.description !== undefined) dto.description = changes.description;
        if (changes.stock !== undefined) dto.stock = changes.stock;
        if (changes.imageFileName !== undefined) dto.image = changes.imageFileName;
        if (changes.isAvailable !== undefined) dto.available = changes.isAvailable;
        if (changes.categoryId !== undefined) dto.categoryId = changes.categoryId || "";

        const response = await api.patch<ProductResponseDTO>(`/products/${id}`, dto);

        return mapToDomain(response.data);
    },

    /**
     * Elimina un producto del backend dado su ID. El backend marcará el producto como
     * eliminado sin removerlo físicamente de la base de datos.
     * @param id ID del producto a eliminar.
     * @returns Promesa que se resuelve cuando la eliminación se ha completado exitosamente.
     */
    async delete(id: string): Promise<void> {
        await api.delete(`/products/${id}`);
    },

    /**
     * Filtra una lista de productos localmente según el ID de su categoría.
     * @param products Lista de productos a filtrar
     * @param categoryId ID de la categoría seleccionada
     * @returns Lista de productos que pertenecen a la categoría especificada.
     */
    filterByCategory(products: IProduct[], categoryId: string | string): IProduct[] {
        const categoryIdStr = categoryId.toString();

        return products.filter((prod: IProduct): boolean => prod.categoryId?.toString() === categoryIdStr);
    },

    /**
     * Filtra localmente combinando búsqueda por texto y categoría, ideal para cambios veloces
     * en el catálogo del Front.
     * @param products Lista original de productos devuelta por el servidor.
     * @param criteria Objeto con el término de búsqueda de input y categoría seleccionada.
     */
    applyFilters(products: IProduct[], criteria: { searchTerm: string; categoryId: string }): IProduct[] {
        const { searchTerm, categoryId } = criteria;
        const term = searchTerm.toLowerCase().trim();

        return products.filter((prod): boolean => {
            // Criterio 1: Coincidencia por nombre
            const matchesName = prod.name.toLowerCase().includes(term);

            // Criterio 2: Coincidencia por categoría única del backend
            const matchesCategory: boolean = categoryId === "all" || prod.categoryId?.toString() === categoryId;

            return matchesName && matchesCategory;
        });
    },
};
