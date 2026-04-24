/**
 *  Interfaz que define la estructura de una categoría de productos
 */
export interface ICategory {
    id: number;
    eliminado: boolean;
    createdAt: string;
    nombre: string;
    descripcion: string;
}
