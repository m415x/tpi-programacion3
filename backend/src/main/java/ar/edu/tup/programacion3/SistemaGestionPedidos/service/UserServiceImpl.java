package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.User;
import ar.edu.tup.programacion3.SistemaGestionPedidos.mapper.UserMapper;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

	@Override
    @Transactional
    public UserResponseDTO save(UserRequestDTO userRequestDTO) {

        User user = userMapper.toEntity(userRequestDTO);
        user = userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {

        User user = userRepository.findByIdOrThrow(id);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {

        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Override
    @Transactional
    public UserResponseDTO update(UserRequestDTO userRequestDTO, Long id) {

        User user = userRepository.findByIdOrThrow(id);

        userMapper.updateUserFromEdit(userRequestDTO, user);
        user = userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        User user = userRepository.findByIdOrThrow(id);

        user.setDeleted(true);

        if (user.getOrders() != null) {
            user.getOrders().forEach(order -> order.setDeleted(true));
        }

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findUsersByName(String name) {

        return userRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findUserByEmail(String email) {

        User user =
                userRepository
                        .findByEmailIgnoreCase(email)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró un usuario activo con el email:"
                                                        + email));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserWithMoreOrders() {

        User user =
                userRepository
                        .getUserWithMoreOrders()
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontraron usuarios con orders registrados en el sistema."));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findHistoricalUser(Long id) {

        User deletedUser = userRepository.findWithDeletedByIdOrThrow(id);

        return userMapper.toDto(deletedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getHistoricalUsers() {

        List<User> allHistory = userRepository.findWithDeletedBy();

        return allHistory.stream().map(userMapper::toDto).toList();
    }
}
