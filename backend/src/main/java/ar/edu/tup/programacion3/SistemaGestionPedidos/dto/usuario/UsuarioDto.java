package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.usuario;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.pedido.PedidoDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Usuario;
import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.Rol;

import java.util.Set;
import java.util.stream.Collectors;

public record UsuarioDto(
        Long id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        Rol rol,
        Set<PedidoDto> pedidos) {
    public static UsuarioDto toDto(Usuario usuario) {
        return new UsuarioDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRol(),
                usuario.getPedidos() != null
                        ? usuario.getPedidos().stream()
                                .map(pedido -> PedidoDto.toDto(pedido, usuario.getId()))
                                .collect(Collectors.toSet())
                        : Set.of());
    }
}
