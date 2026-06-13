package ar.edu.tup.programacion3.SistemaGestionPedidos.dto;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.*;
import java.math.BigDecimal;

public record ProductRequestDTO(
        @ValidName String name,
        @ValidAmount BigDecimal price,
        @ValidLongText String description,
        @ValidQty Integer stock,
        @ValidLongText String image,
        @ValidNotNull Boolean available,
        Long categoryId) {}
