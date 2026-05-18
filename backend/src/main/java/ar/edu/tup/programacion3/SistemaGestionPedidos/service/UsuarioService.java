package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.usuario.UsuarioCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.usuario.UsuarioDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.usuario.UsuarioEdit;

import java.util.List;

public interface UsuarioService {
    public UsuarioDto save(UsuarioCreate usuarioCreate);

    public UsuarioDto findById(Long id);

    public List<UsuarioDto> findAll();

    public UsuarioDto update(UsuarioEdit usuarioEdit, Long id);

    public void deleteById(Long id);

    public List<UsuarioDto> findUsersByName(String name);

    public UsuarioDto findUserByEmail(String email);

	UsuarioDto getUserWithMoreOrders();
}
