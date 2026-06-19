package ar.edu.tup.programacion3.SGP.service;

import ar.edu.tup.programacion3.SGP.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.UserResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserService {
    public UserResponseDTO save(UserRequestDTO dto);

    public UserResponseDTO findById(UUID id);

    public List<UserResponseDTO> findAll();

    public UserResponseDTO update(UserRequestDTO dto, UUID id);

	public UserResponseDTO partialUpdate(UserRequestDTO dto, UUID id);

    public void deleteById(UUID id);

    public List<UserResponseDTO> findUsersByName(String name);

    public UserResponseDTO findUserByEmail(String email);

	public boolean verifyCredentials(String email, String encryptedPasswordFromFront);

	public UserResponseDTO getUserWithMoreOrders();

    public UserResponseDTO findHistoricalUser(UUID id);

    public List<UserResponseDTO> getHistoricalUsers();

	public void validateAdminCredentialsOrThrow(UUID id, String email);
}
