import type { IOrder } from "@interfaces/Order.interface";
import type { OrderStatus, PaymentMethod } from "@interfaces/Enums";
import api from "@services/api";

/**
 * DTO que refleja la estructura exacta que devuelve el backend de Spring Boot,
 * incorporando los campos solicitados por el docente para el flujo de entrega.
 */
interface OrderResponseDTO {
    id: string;
    createdAt: string;
    isDeleted: boolean;
    totalPrice: number;
    status: OrderStatus;
    paymentMethod: PaymentMethod;
    customerPhone: string | null;
    shippingAddress: string | null;
    customerNotes: string | null;
    user: {
        id: string;
        firstName: string;
        lastName: string;
        email: string;
    } | null;
}

/**
 * Función de mapeo quirúrgico que transforma los objetos de la API REST
 * al modelo de dominio estricto del Frontend.
 */
const mapToDomain = (dto: OrderResponseDTO): IOrder => ({
    id: dto.id,
    createdAt: dto.createdAt,
    isDeleted: dto.isDeleted,
    totalPrice: dto.totalPrice,
    status: dto.status,
    paymentMethod: dto.paymentMethod,
    customerPhone: dto.customerPhone || "No provisto",
    shippingAddress: dto.shippingAddress || "Retiro en sucursal",
    customerNotes: dto.customerNotes || "Sin observaciones.",
    // Armamos un nombre unificado amigable si existe el objeto user
    user: dto.user
        ? {
              id: dto.user.id,
              name: `${dto.user.firstName} ${dto.user.lastName}`,
              email: dto.user.email,
          }
        : null,
});

/**
 * Servicio para gestionar las peticiones HTTP de Pedidos (Órdenes) con el Backend Core.
 */
export const orderService = {
    /**
     * Trae todos los pedidos del sistema para el panel del administrador.
     * @returns Promesa con la lista de órdenes procesadas y mapeadas al dominio.
     */
    async getAll(): Promise<IOrder[]> {
        // Realiza la petición perimetral (Axios inyectará los headers X-User-Id mediante el interceptor)
        const response = await api.get<OrderResponseDTO[]>("/orders");
        return response.data.map(mapToDomain);
    },

    /**
     * Recupera un pedido específico de la base de datos por su ID único (UUID).
     * @param id Identificador de la orden
     */
    async getById(id: string): Promise<IOrder> {
        const response = await api.get<OrderResponseDTO>(`/orders/${id}`);
        return mapToDomain(response.data);
    },

    /**
     * Actualiza el estado logístico de un pedido (Manejado desde el pop-up del admin).
     * Aplica un método PATCH o PUT optimizado según los endpoints del controlador de Java.
     * @param id ID del pedido a modificar
     * @param status Nuevo estado (PENDING, PREPARING, DELIVERED, CANCELLED)
     */
    async updateStatus(id: string, status: OrderStatus): Promise<IOrder> {
        // Le pegamos al endpoint parcial del backend enviando el nuevo estado en el body
        const response = await api.patch<OrderResponseDTO>(`/orders/${id}/status`, { status });
        return mapToDomain(response.data);
    },
};
