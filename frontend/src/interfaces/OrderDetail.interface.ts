import type { IProduct } from "@interfaces/Product.interface";

/**
 * Interfaz que define la estructura de una orden de compra
 */
export interface IOrderDetail {
    id: string;
    deleted: boolean;
    createdAt: string;
    quantity: number;
    subtotal: number;
    product: IProduct;
    orderId?: string | null;
}
