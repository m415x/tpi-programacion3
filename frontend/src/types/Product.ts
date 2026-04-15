import type { ICategory } from "@interfaces/ICategory";

// Interfaz que define la estructura de un Producto en la tienda
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
