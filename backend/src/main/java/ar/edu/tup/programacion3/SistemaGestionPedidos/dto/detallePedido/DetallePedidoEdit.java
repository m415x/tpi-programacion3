package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.detallePedido;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidQty;

public record DetallePedidoEdit(@ValidQty Integer cantidad, Long idPedido) {}
