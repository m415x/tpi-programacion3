import type { ICategory } from "@interfaces/ICategory";

/**
 * Interfaz que define la estructura de un producto en el sistema
 */
export interface Product {
    id: number;
    eliminado: boolean;
    createdAt: string;
    nombre: string;
    precio: number;
    descripcion: string;
    stock: number;
    imagen: string;
    disponible: boolean;
    categorias: (ICategory | undefined)[];
}
