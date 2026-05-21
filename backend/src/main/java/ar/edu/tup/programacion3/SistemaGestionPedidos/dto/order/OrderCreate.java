package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.order;

import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.OrderStatus;
import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.PaymentMethod;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidAmount;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidNotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderCreate(
        @ValidNotNull(message = "La fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                LocalDate date,
        @ValidNotNull OrderStatus orderStatus,
        @ValidAmount(message = "total") BigDecimal total,
        @ValidNotNull(message = "La forma de pago") PaymentMethod paymentMethod,
        Long userId) {}
