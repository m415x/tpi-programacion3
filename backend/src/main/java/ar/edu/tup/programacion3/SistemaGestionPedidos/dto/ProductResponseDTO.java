package ar.edu.tup.programacion3.SistemaGestionPedidos.dto;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        BigDecimal price,
        String description,
        Integer stock,
        String image,
        Boolean available,
        Long categoryId) {}
