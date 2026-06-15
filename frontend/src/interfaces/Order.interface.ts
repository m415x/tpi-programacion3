import type { OrderStatus, PaymentMethod } from "@/interfaces/Enums";
import type { IOrderDetail } from "@/interfaces/OrderDetail.interface";

/**
 * Interfaz que define la estructura de una orden de compra
 */
export interface IOrder {
    id: number;
    deleted: boolean;
    createdAt: string;
    date: string;
    orderStatus: OrderStatus;
    total: number;
    paymentMethod: PaymentMethod;
    userId: number;
    orderDetails: IOrderDetail[];
}
