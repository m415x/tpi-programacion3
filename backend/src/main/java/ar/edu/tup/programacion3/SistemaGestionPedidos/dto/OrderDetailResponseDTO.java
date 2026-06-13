package ar.edu.tup.programacion3.SistemaGestionPedidos.dto;

import java.math.BigDecimal;

public record OrderDetailResponseDTO(
		Long id, Integer quantity, BigDecimal subtotal, ProductResponseDTO product, Long orderId) {}
