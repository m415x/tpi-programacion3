import type { IOrder } from "@interfaces/Order.interface";
import type { OrderStatus, PaymentMethod } from "@interfaces/Enums";
import api from "@services/api";

/**
 * DTO que define la estructura exacta que el backend de Spring Boot
 * espera recibir para persistir un nuevo pedido en la base de datos.
 */
interface OrderRequestDTO {
    date: string;
    orderStatus: OrderStatus;
    total: number;
    paymentMethod: PaymentMethod;
    userId: string;
    customerPhone: string;
    shippingAddress: string;
    customerNotes: string;
    orderDetail: any[];
}

/**
 * DTO que refleja la estructura exacta que devuelve el backend de Spring Boot,
 * incorporando los campos solicitados por el docente para el flujo de entrega.
 */
interface OrderResponseDTO {
    id: string;
    createdAt: string;
    updatedAt?: string;
    version?: number;
    orderStatus: OrderStatus;
    total: number;
    paymentMethod: PaymentMethod;
    userId: string;
    customerPhone: string | null;
    shippingAddress: string | null;
    customerNotes: string | null;
    orderDetails: any[];
}

/**
 * Función de mapeo quirúrgico que transforma los objetos de la API REST
 * al modelo de dominio estricto del Frontend.
 */
const mapToDomain = (dto: OrderResponseDTO): IOrder => ({
    id: dto.id,
    isDeleted: false,
    createdAt: dto.createdAt,
    updatedAt: dto.updatedAt,
    version: dto.version,
    totalPrice: dto.total,
    status: dto.orderStatus,
    paymentMethod: dto.paymentMethod,
    customerPhone: dto.customerPhone || "No provisto",
    shippingAddress: dto.shippingAddress || "Retiro en sucursal",
    customerNotes: dto.customerNotes || "Sin observaciones.",
    user: dto.userId
        ? {
              id: dto.userId,
              name: "Cliente Activo",
              email: "",
          }
        : null,
    orderDetails: (dto.orderDetails || []).map((detail: any) => ({
        id: detail.id,
        deleted: detail.isDeleted || detail.deleted || false,
        createdAt: detail.date || dto.createdAt,
        quantity: detail.quantity,
        subtotal: detail.subtotal || detail.quantity * (detail.product?.price || 0),
        product: detail.product,
        orderId: dto.id,
    })),
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
     * Trae exclusivamente las comandas pertenecientes al cliente logueado actualmente.
     * @returns Promesa con la lista de órdenes procesadas y mapeadas al dominio.
     */
    async getMyOrders(): Promise<IOrder[]> {
        // Tu interceptor de Axios se encarga de inyectar las credenciales en los headers automáticamente.
        const response = await api.get<OrderResponseDTO[]>("/orders/my-orders");

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
     * Envía un nuevo pedido al backend para su persistencia transaccional.
     * @param orderPayload Estructura de datos completa recolectada del Checkout
     * @returns Promesa con el objeto IOrder de dominio devuelto y confirmado por la base de datos.
     */
    async create(orderPayload: OrderRequestDTO): Promise<IOrder> {
        // Ejecuta el POST enviando el DTO completo alineado a los mapeadores de Java
        const response = await api.post<OrderResponseDTO>("/orders", orderPayload);

        return mapToDomain(response.data);
    },

    /**
     * Agrega un ítem a una orden existente
     * @param orderId ID de la orden a la que seagregará el ítem.
     * @param productId ID del producto a agregar.
     * @param qty Cantidad del producto a agregar.
     */
    async addItemToOrder(orderId: string, productId: string, qty: number): Promise<void> {
        await api.post(`/orders/${orderId}/items`, null, {
            params: {
                productId: productId,
                qty: qty,
            },
        });
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

    /**
     * Envía la petición al backend para cancelar un pedido pendiente.
     */
    async cancel(id: string): Promise<IOrder> {
        const response = await api.patch<OrderResponseDTO>(`/orders/${id}/cancel`);

        return mapToDomain(response.data);
    },
};
