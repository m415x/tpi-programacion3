package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.orderDetail;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductDto;
import java.math.BigDecimal;

public record OrderDetailDto(
		Long id, Integer quantity, BigDecimal subtotal, ProductDto product, Long orderId) {}
