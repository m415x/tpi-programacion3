package ar.edu.tup.programacion3.SGP.dto;

import ar.edu.tup.programacion3.SGP.validator.*;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequestDTO(
        @ValidName String name,
        @ValidAmount BigDecimal price,
        @ValidLongText String description,
        @ValidQty Integer stock,
        @ValidLongText String image,
        @ValidNotNull Boolean available,
        UUID categoryId) {}
