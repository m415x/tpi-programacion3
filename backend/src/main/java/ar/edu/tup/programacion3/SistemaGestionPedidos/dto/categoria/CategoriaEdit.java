package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.categoria;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Categoria;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidLongText;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidName;

public record CategoriaEdit(@ValidName String nombre, @ValidLongText String descripcion) {

    public void applyTo(Categoria categoria) {

        if (this.nombre != null) {
            categoria.setNombre(this.nombre);
        }
        if (this.descripcion != null) {
            categoria.setDescripcion(this.descripcion);
        }
    }
}
