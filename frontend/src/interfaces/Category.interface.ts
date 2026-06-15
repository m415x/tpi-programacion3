/**
 *  Interfaz que define la estructura de una categoría de productos
 */
export interface ICategory {
    id: number;
    isDeleted: boolean;
    createdAt: string;
    name: string;
    description: string;
}
