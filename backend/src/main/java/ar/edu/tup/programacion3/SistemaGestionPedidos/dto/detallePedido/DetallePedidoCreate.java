package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.detallePedido;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidNotNull;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidQty;

public record DetallePedidoCreate(
        @ValidQty Integer cantidad,
        @ValidNotNull(message = "El producto") Long idProducto,
        Long idPedido) {}
