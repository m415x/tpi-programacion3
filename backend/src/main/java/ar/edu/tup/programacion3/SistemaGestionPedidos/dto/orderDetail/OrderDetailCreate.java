package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.orderDetail;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidNotNull;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidQty;

public record OrderDetailCreate(
        @ValidQty Integer quantity,
        @ValidNotNull(message = "El producto") Long productId,
        Long orderId) {}
