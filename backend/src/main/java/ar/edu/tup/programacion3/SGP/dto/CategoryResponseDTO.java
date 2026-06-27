package ar.edu.tup.programacion3.SGP.dto;

import java.util.Set;
import java.util.UUID;

public record CategoryResponseDTO(
        UUID id, String name, String description, String image, Set<ProductResponseDTO> products) {}
