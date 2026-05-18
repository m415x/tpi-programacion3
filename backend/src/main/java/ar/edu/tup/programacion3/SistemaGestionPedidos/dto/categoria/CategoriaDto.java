package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.categoria;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.producto.ProductoDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Categoria;

import java.util.Set;
import java.util.stream.Collectors;

public record CategoriaDto(Long id, String nombre, String descripcion, Set<ProductoDto> productos) {

    public static CategoriaDto toDto(Categoria categoria) {

        return new CategoriaDto(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getProductos() != null
                        ? categoria.getProductos().stream()
                                .map(producto -> ProductoDto.toDto(producto, categoria.getId()))
                                .collect(Collectors.toSet())
                        : Set.of());
    }
}
