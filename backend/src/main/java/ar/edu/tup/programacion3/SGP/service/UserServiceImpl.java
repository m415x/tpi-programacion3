package ar.edu.tup.programacion3.SGP.service;

import ar.edu.tup.programacion3.SGP.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.UserResponseDTO;
import ar.edu.tup.programacion3.SGP.model.User;
import ar.edu.tup.programacion3.SGP.mapper.UserMapper;
import ar.edu.tup.programacion3.SGP.model.enums.UserRole;
import ar.edu.tup.programacion3.SGP.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UserResponseDTO save(UserRequestDTO dto) {

        User user = mapper.toEntity(dto);
        user = repository.save(user);

        return mapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findById(UUID id) {

        User user = repository.findByIdOrThrow(id);

        return mapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {

        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional
    public UserResponseDTO update(UserRequestDTO dto, UUID id) {

        User user = repository.findByIdOrThrow(id);

        mapper.updateUserFromEdit(dto, user);
        user = repository.save(user);

        return mapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDTO partialUpdate(UserRequestDTO dto, UUID id) {

        User user = repository.findByIdOrThrow(id);

        mapper.updateUserFromEdit(dto, user);
        user = repository.save(user);

        return mapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {

        User user = repository.findByIdOrThrow(id);

        user.setDeleted(true);

        if (user.getOrders() != null) {
            user.getOrders().forEach(order -> order.setDeleted(true));
        }

        repository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findUsersByName(String name) {

        return repository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findUserByEmail(String email) {

        User user =
                repository
                        .findByEmailIgnoreCase(email)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró un usuario activo con el email:"
                                                        + email));

        return mapper.toDto(user);
    }

    @Override
    @Transactional
    public boolean verifyCredentials(String email, String encryptedPasswordFromFront) {
        // Buscamos la entidad de dominio real (la que tiene el password, no el DTO)
        User userEntity = repository.findByEmailIgnoreCase(email).orElse(null);

        if (userEntity == null) {
            return false;
        }

        // Comparamos los dos hashes SHA-256. Al ser strings idénticos, calza perfecto.
        return userEntity.getPassword().equals(encryptedPasswordFromFront);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserWithMoreOrders() {

        User user =
                repository
                        .getUserWithMoreOrders()
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontraron usuarios con orders registrados en el sistema."));

        return mapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findHistoricalUser(UUID id) {

        User deletedUser = repository.findDeletedByIdOrThrow(id);

        return mapper.toDto(deletedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getHistoricalUsers() {

        List<User> allHistory = repository.findDeletedAll();

        return allHistory.stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public void validateAdminCredentialsOrThrow(UUID userId, String email) {
        // Buscamos la entidad real en la base de datos
        User user =
                repository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new UnsupportedOperationException(
                                                "Acceso denegado: El usuario no existe en la base de datos. Por favor, cierre la sesión y vuelva a iniciarla."));

        // Validamos de forma estricta: coincidencia de email y rol de administrador
        if (!user.getEmail().equalsIgnoreCase(email.trim())
                || user.getUserRole() != UserRole.ADMIN) {
            throw new UnsupportedOperationException(
                    "Acceso denegado: Credenciales administrativas inválidas.");
        }
    }
}