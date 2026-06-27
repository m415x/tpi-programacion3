package ar.edu.tup.programacion3.SGP.dto;

import ar.edu.tup.programacion3.SGP.model.enums.OrderStatus;
import ar.edu.tup.programacion3.SGP.model.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record OrderResponseDTO(
        UUID id,
        LocalDateTime createdAt,
        OrderStatus orderStatus,
        BigDecimal total,
        PaymentMethod paymentMethod,
        UUID userId,
        String customerPhone,
        String shippingAddress,
        String customerNotes,
        Set<OrderDetailResponseDTO> orderDetails) {}
