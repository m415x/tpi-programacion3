package ar.edu.tup.programacion3.SistemaGestionPedidos.controller;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.UserService;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponseDTO saveUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return userService.save(userRequestDTO);
    }

    @GetMapping("/{id}")
    public UserResponseDTO findUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping
    public List<UserResponseDTO> findUsers() {
        return userService.findAll();
    }

    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userRequestDTO) {
        return userService.update(userRequestDTO, id);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);

        return "Usuario con id " + id + " eliminado!";
    }

    @GetMapping("/name/{name}")
    public List<UserResponseDTO> findUsersByName(@PathVariable String name) {
        return userService.findUsersByName(name);
    }

    @GetMapping("/email/{email}")
    public UserResponseDTO findUserByEmail(@PathVariable String email) {
        return userService.findUserByEmail(email);
    }
}
