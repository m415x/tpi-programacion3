package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.user.UserCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.user.UserDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.user.UserEdit;
import java.util.List;

public interface UserService {
    public UserDto save(UserCreate userCreate);

    public UserDto findById(Long id);

    public List<UserDto> findAll();

    public UserDto update(UserEdit userEdit, Long id);

    public void deleteById(Long id);

    public List<UserDto> findUsersByName(String name);

    public UserDto findUserByEmail(String email);

	UserDto getUserWithMoreOrders();
}
