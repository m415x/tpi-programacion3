import type { ICategory } from "@interfaces/ICategory";

/**
 * Interfaz que define la estructura de un producto en el sistema
 */
export interface IProduct {
    id: number;
    deleted: boolean;
    createdAt: string;
    name: string;
    price: number;
    description: string;
    stock: number;
    image: string;
    available: boolean;
    categories: (ICategory | undefined)[];
}
