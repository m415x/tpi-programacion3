package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.producto;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Producto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.*;

import java.math.BigDecimal;

public record ProductoEdit(
        @ValidName String nombre,
        @ValidAmount(message = "precio") BigDecimal precio,
        @ValidLongText String descripcion,
        @ValidQty(message = "El stock") Integer stock,
        @ValidLongText(message = "La imagen") String imagen,
        @ValidNotNull Boolean disponible,
        Long idCategoria) {

    public void applyTo(Producto producto) {

        if (this.nombre != null) {
            producto.setNombre(this.nombre);
        }
        if (this.precio != null) {
            producto.setPrecio(this.precio);
        }
        if (this.descripcion != null) {
            producto.setDescripcion(this.descripcion);
        }
        if (this.stock != null) {
            producto.setStock(this.stock);
        }
        if (this.imagen != null) {
            producto.setImagen(this.imagen);
        }
        if (this.disponible != null) {
            producto.setDisponible(this.disponible);
        }
    }
}
