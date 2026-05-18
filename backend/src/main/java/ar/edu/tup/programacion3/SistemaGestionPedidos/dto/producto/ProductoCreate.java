package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.producto;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Producto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.*;

import java.math.BigDecimal;

public record ProductoCreate(
        @ValidName String nombre,
        @ValidAmount(message = "precio") BigDecimal precio,
        @ValidLongText String descripcion,
        @ValidQty(message = "El stock") Integer stock,
        @ValidLongText(message = "La imagen") String imagen,
        @ValidNotNull Boolean disponible,
        Long idCategoria) {

    public Producto toEntity() {

        return Producto.builder()
                .nombre(this.nombre)
                .precio(this.precio)
                .descripcion(this.descripcion)
                .stock(this.stock)
                .imagen(this.imagen)
                .disponible(this.disponible)
                .build();
    }
}
