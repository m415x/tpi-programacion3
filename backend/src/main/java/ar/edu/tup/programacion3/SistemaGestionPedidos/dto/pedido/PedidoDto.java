package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.pedido;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.detallePedido.DetallePedidoDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Pedido;
import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.Estado;
import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.FormaPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

public record PedidoDto(
        Long id,
        LocalDate fecha,
        Estado estado,
        BigDecimal total,
        FormaPago formaPago,
        Long idUsuario,
        Set<DetallePedidoDto> detallePedidos) {

    public static PedidoDto toDto(Pedido pedido, Long idUsuario) {
        return new PedidoDto(
                pedido.getId(),
                pedido.getFecha(),
                pedido.getEstado(),
                pedido.getTotal(),
                pedido.getFormaPago(),
                idUsuario,
                pedido.getDetallePedido() != null
                        ? pedido.getDetallePedido().stream()
                                .map(
                                        detalle ->
                                                DetallePedidoDto.toDto(
                                                        detalle, pedido.getId(), null))
                                .collect(Collectors.toSet())
                        : Set.of());
    }
}
