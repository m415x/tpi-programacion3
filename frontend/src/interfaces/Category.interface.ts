/**
 *  Interfaz que define la estructura de una categoría de productos
 */
export interface ICategory {
    id: string;
    isDeleted: boolean;
    createdAt: string;
    updatedAt?: string | undefined;
    version?: number | undefined;
    name: string;
    description: string | null;
    image: string;
}
