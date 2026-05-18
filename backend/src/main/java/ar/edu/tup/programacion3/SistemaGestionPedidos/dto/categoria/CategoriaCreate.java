package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.categoria;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Categoria;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidLongText;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidName;

public record CategoriaCreate(@ValidName String nombre, @ValidLongText String descripcion) {

    public Categoria toEntity() {

        return Categoria.builder().nombre(this.nombre).descripcion(this.descripcion).build();
    }
}
