package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.pedido;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Pedido;
import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.Estado;
import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.FormaPago;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidAmount;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidNotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PedidoCreate(
        @ValidNotNull(message = "La fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                LocalDate fecha,
        @ValidNotNull Estado estado,
        @ValidAmount(message = "total") BigDecimal total,
        @ValidNotNull(message = "La forma de pago") FormaPago formaPago,
        Long idUsuario) {
    public Pedido toEntity() {
        return Pedido.builder()
                .fecha(this.fecha)
                .estado(this.estado)
                .total(this.total)
                .formaPago(this.formaPago)
                .build();
    }
}
