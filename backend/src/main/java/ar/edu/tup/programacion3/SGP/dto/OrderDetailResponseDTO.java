package ar.edu.tup.programacion3.SGP.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderDetailResponseDTO(
		UUID id, Integer quantity, BigDecimal subtotal, ProductResponseDTO product, UUID orderId) {}
