package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.usuario;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Usuario;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidEmail;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidName;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidPassword;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidPhone;

public record UsuarioEdit(
        @ValidName String nombre,
        @ValidName(message = "apellido") String apellido,
        @ValidEmail String email,
        @ValidPhone String telefono,
        @ValidPassword String contraseña) {
    public void applyTo(Usuario usuario) {
        if (this.nombre != null) {
            usuario.setNombre(this.nombre);
        }
        if (this.apellido != null) {
            usuario.setApellido(this.apellido);
        }
        if (this.email != null) {
            usuario.setEmail(this.email);
        }
        if (this.telefono != null) {
            usuario.setTelefono(this.telefono);
        }
        if (this.contraseña != null) {
            usuario.setContraseña(this.contraseña);
        }
    }
}
