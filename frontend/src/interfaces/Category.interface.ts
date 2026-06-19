/**
 *  Interfaz que define la estructura de una categoría de productos
 */
export interface ICategory {
    id: string;
    isDeleted: boolean;
    createdAt: string;
    name: string;
    description: string;
}
