package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.order;

import ar.edu.tup.programacion3.SistemaGestionPedidos.model.enums.OrderStatus;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.enums.PaymentMethod;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidAmount;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidNotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record OrderEdit(
        @ValidNotNull(message = "La fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                LocalDate date,
        @ValidNotNull OrderStatus orderStatus,
        @ValidAmount(message = "total") BigDecimal total,
        @ValidNotNull(message = "La forma de pago") PaymentMethod paymentMethod,
        Long userId) {}
