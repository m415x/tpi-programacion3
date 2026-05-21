package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String name,
        BigDecimal price,
        String description,
        Integer stock,
        String image,
        Boolean available,
        Long categoryId) {}
