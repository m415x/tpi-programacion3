package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.producto;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Producto;

import java.math.BigDecimal;

public record ProductoDto(
        Long id,
        String nombre,
        BigDecimal precio,
        String descripcion,
        Integer stock,
        String imagen,
        Boolean disponible,
        Long idCategoria) {

    public static ProductoDto toDto(Producto producto, Long idCategoria) {

        return new ProductoDto(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getDescripcion(),
                producto.getStock(),
                producto.getImagen(),
                producto.getDisponible(),
                idCategoria);
    }
}
