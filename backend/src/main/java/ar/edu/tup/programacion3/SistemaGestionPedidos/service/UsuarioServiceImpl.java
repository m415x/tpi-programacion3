package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.usuario.UsuarioCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.usuario.UsuarioDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.usuario.UsuarioEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Usuario;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public UsuarioDto save(UsuarioCreate usuarioCreate) {

        Usuario usuario = usuarioCreate.toEntity();
        usuario = usuarioRepository.save(usuario);

        return UsuarioDto.toDto(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDto findById(Long id) {

        Usuario usuario =
                usuarioRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró usuario con id: " + id));

        return UsuarioDto.toDto(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDto> findAll() {

        return usuarioRepository.findAll().stream().map(UsuarioDto::toDto).toList();
    }

    @Override
    @Transactional
    public UsuarioDto update(UsuarioEdit usuarioEdit, Long id) {

        Usuario usuario =
                usuarioRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró usuario con id: " + id));

        usuarioEdit.applyTo(usuario);
        usuario = usuarioRepository.save(usuario);

        return UsuarioDto.toDto(usuario);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        Usuario usuario =
                usuarioRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró usuario con id: " + id));

        usuario.setEliminado(true);

        if (usuario.getPedidos() != null) {
            usuario.getPedidos().forEach(pedido -> pedido.setEliminado(true));
        }

        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDto> findUsersByName(String name) {

        return usuarioRepository
                .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(name, name)
                .stream()
                .map(UsuarioDto::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDto findUserByEmail(String email) {

        Usuario usuario =
                usuarioRepository
                        .findByEmailIgnoreCase(email)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró un usuario activo con el email:"
                                                        + email));

        return UsuarioDto.toDto(usuario);
    }

    @Override
    public UsuarioDto getUserWithMoreOrders() {

        Usuario usuario =
                usuarioRepository
                        .getUserWithMoreOrders()
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontraron usuarios con pedidos registrados en el sistema."));

        return UsuarioDto.toDto(usuario);
    }
}
