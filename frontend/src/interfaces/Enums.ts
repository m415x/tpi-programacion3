/**
 * Enum que define los roles de usuario en el sistema
 */
export enum UserRole {
    ADMIN = "ADMIN",
    CLIENT = "CLIENT",
}

/**
 * Enum que define los estados de una orden de compra
 */
export enum OrderStatus {
    PENDING = "PENDING",
    CONFIRMED = "CONFIRMED",
    COMPLETED = "COMPLETED",
    CANCELLED = "CANCELLED",
}

/**
 * Enum que define los métodos de pago disponibles
 */
export enum PaymentMethod {
    CARD = "CARD",
    TRANSFER = "TRANSFER",
    CASH = "CASH",
}
