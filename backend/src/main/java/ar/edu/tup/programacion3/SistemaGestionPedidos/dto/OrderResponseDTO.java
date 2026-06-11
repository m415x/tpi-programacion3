package ar.edu.tup.programacion3.SistemaGestionPedidos.dto;

import ar.edu.tup.programacion3.SistemaGestionPedidos.model.enums.OrderStatus;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record OrderResponseDTO(
        Long id,
        LocalDate date,
        OrderStatus orderStatus,
        BigDecimal total,
        PaymentMethod paymentMethod,
        Long userId,
        Set<OrderDetailResponseDTO> orderDetails) {}
