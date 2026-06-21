package ar.edu.tup.programacion3.SGP.dto;

import ar.edu.tup.programacion3.SGP.model.enums.OrderStatus;
import ar.edu.tup.programacion3.SGP.model.enums.PaymentMethod;
import ar.edu.tup.programacion3.SGP.validator.ValidNotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record OrderRequestDTO(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @ValidNotNull OrderStatus orderStatus,
        BigDecimal total,
        @ValidNotNull PaymentMethod paymentMethod,
        UUID userId,
        String customerPhone,
        String shippingAddress,
        String customerNotes) {}
