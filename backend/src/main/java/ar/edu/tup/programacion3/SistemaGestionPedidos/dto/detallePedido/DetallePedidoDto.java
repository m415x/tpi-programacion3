package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.detallePedido;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.producto.ProductoDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.DetallePedido;

import java.math.BigDecimal;

public record DetallePedidoDto(
        Long id, Integer cantidad, BigDecimal subtotal, ProductoDto producto, Long idPedido) {

    public static DetallePedidoDto toDto(
            DetallePedido detallePedido, Long idPedido, Long idCategoriaProducto) {

        return new DetallePedidoDto(
                detallePedido.getId(),
                detallePedido.getCantidad(),
                detallePedido.getSubtotal(),
                detallePedido.getProducto() != null
                        ? ProductoDto.toDto(detallePedido.getProducto(), idCategoriaProducto)
                        : null,
                idPedido);
    }
}
