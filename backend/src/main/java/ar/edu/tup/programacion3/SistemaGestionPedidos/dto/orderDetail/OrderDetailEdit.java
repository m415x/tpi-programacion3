package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.orderDetail;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidQty;

public record OrderDetailEdit(@ValidQty Integer quantity, Long orderId) {}
