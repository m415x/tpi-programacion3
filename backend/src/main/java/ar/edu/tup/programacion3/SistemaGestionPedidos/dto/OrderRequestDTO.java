package ar.edu.tup.programacion3.SistemaGestionPedidos.dto;

import ar.edu.tup.programacion3.SistemaGestionPedidos.model.enums.OrderStatus;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.enums.PaymentMethod;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidAmount;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidNotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderRequestDTO(
        @ValidNotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @ValidNotNull OrderStatus orderStatus,
        @ValidAmount BigDecimal total,
        @ValidNotNull PaymentMethod paymentMethod,
        Long userId) {}
