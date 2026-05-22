package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.user.UserCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.user.UserDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.user.UserEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.User;
import ar.edu.tup.programacion3.SistemaGestionPedidos.mapper.UserMapper;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserDto save(UserCreate userCreate) {

        User user = userMapper.toEntity(userCreate);
        user = userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {

        User user = userRepository.findByIdOrThrow(id);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {

        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Override
    @Transactional
    public UserDto update(UserEdit userEdit, Long id) {

        User user = userRepository.findByIdOrThrow(id);

        userMapper.updateUserFromEdit(userEdit, user);
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
    public List<UserDto> findUsersByName(String name) {

        return userRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findUserByEmail(String email) {

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
    public UserDto getUserWithMoreOrders() {

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
    public UserDto findHistoricalUser(Long id) {

        User deletedUser = userRepository.findWithDeletedByIdOrThrow(id);

        return userMapper.toDto(deletedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getHistoricalUsers() {

        List<User> allHistory = userRepository.findWithDeletedBy();

        return allHistory.stream().map(userMapper::toDto).toList();
    }
}
