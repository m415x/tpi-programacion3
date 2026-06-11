package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    public UserResponseDTO save(UserRequestDTO userRequestDTO);

    public UserResponseDTO findById(Long id);

    public List<UserResponseDTO> findAll();

    public UserResponseDTO update(UserRequestDTO userRequestDTO, Long id);

    public void deleteById(Long id);

    public List<UserResponseDTO> findUsersByName(String name);

    public UserResponseDTO findUserByEmail(String email);

    UserResponseDTO getUserWithMoreOrders();

    UserResponseDTO findHistoricalUser(Long id);

    List<UserResponseDTO> getHistoricalUsers();
}
