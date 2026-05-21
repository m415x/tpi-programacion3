package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.order;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.orderDetail.OrderDetailDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.OrderStatus;
import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record OrderDto(
        Long id,
        LocalDate date,
        OrderStatus orderStatus,
        BigDecimal total,
        PaymentMethod paymentMethod,
        Long userId,
        Set<OrderDetailDto> orderDetails) {}
