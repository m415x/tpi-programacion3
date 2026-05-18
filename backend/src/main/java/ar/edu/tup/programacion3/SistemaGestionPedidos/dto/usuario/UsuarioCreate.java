package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.usuario;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Usuario;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidEmail;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidName;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidPassword;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidPhone;

public record UsuarioCreate(
        @ValidName String nombre,
        @ValidName(message = "apellido") String apellido,
        @ValidEmail String email,
        @ValidPhone String telefono,
        @ValidPassword String contraseña) {
    public Usuario toEntity() {
        return Usuario.builder()
                .nombre(this.nombre)
                .apellido(this.apellido)
                .email(this.email)
                .telefono(this.telefono)
                .contraseña(this.contraseña)
                .build();
    }
}
