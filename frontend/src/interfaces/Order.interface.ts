import type { OrderStatus, PaymentMethod } from "@interfaces/Enums";
import type { IOrderDetail } from "@interfaces/OrderDetail.interface";

/**
 * Interfaz que define la estructura de una orden de compra
 */
export interface IOrder {
    id: string;
    createdAt: string;
    isDeleted: boolean;
    totalPrice: number;
    status: any;
    paymentMethod: string;
    customerPhone: string;
    shippingAddress: string;
    customerNotes: string;
    user: { id: string; name: string; email: string } | null;
}
