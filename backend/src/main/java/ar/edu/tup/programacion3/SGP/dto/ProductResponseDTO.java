package ar.edu.tup.programacion3.SGP.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDTO(
        UUID id,
        String name,
        BigDecimal price,
        String description,
        Integer stock,
        String image,
        Boolean available,
        CategoryNestedDTO category) {
	public record CategoryNestedDTO(UUID id, String name, String description, String image) {}
}
