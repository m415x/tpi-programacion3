package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductDto;
import java.util.Set;

public record CategoryDto(Long id, String name, String description, Set<ProductDto> products) {}
