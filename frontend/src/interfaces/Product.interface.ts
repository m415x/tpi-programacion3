/**
 * Interfaz que define la estructura de un producto en el sistema
 */
export interface IProduct {
    id: number;
    isDeleted: boolean;
    createdAt: string;
    name: string;
    price: number;
    description: string;
    stock: number;
    imageUrl: string;
    isAvailable: boolean;
    categoryId: number | null;
}
