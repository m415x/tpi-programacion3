/**
 * Interfaz que define la estructura de un producto en el sistema
 */
export interface IProduct {
    id: string;
    isDeleted: boolean;
    createdAt: string;
    name: string;
    price: number;
    description: string;
    stock: number;
    imageUrl: string;
    isAvailable: boolean;
    categoryId: string | null;
}
