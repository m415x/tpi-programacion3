package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserResponseDTO;
import java.util.List;

public interface UserService {
    public UserResponseDTO save(UserRequestDTO dto);

    public UserResponseDTO findById(Long id);

    public List<UserResponseDTO> findAll();

    public UserResponseDTO update(UserRequestDTO dto, Long id);

	public UserResponseDTO partialUpdate(UserRequestDTO dto, Long id);

    public void deleteById(Long id);

    public List<UserResponseDTO> findUsersByName(String name);

    public UserResponseDTO findUserByEmail(String email);

    public UserResponseDTO getUserWithMoreOrders();

    public UserResponseDTO findHistoricalUser(Long id);

    public List<UserResponseDTO> getHistoricalUsers();
}
