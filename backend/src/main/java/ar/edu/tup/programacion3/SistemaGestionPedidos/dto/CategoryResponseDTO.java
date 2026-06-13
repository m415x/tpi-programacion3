package ar.edu.tup.programacion3.SistemaGestionPedidos.dto;

import java.util.Set;

public record CategoryResponseDTO(Long id, String name, String description, Set<ProductResponseDTO> products) {}
