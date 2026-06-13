package ar.edu.tup.programacion3.SistemaGestionPedidos.controller;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.UserService;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.groups.OnCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.groups.OnUpdate;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public UserResponseDTO saveUser(@Validated(OnCreate.class) @RequestBody UserRequestDTO dto) {

        return service.save(dto);
    }

    @GetMapping("/{id}")
    public UserResponseDTO findUser(@PathVariable Long id) {

		return service.findById(id);
    }

    @GetMapping
    public List<UserResponseDTO> findUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {

        if (email != null && !email.trim().isEmpty()) {
            UserResponseDTO user = service.findUserByEmail(email);

            return user != null ? List.of(user) : List.of();
        }

        if (name != null && !name.trim().isEmpty()) {

			return service.findUsersByName(name);
        }

        return service.findAll();
    }

    @PutMapping("/{id}")
    public UserResponseDTO updateUser(
            @PathVariable Long id, @Valid @RequestBody UserRequestDTO dto) {

        return service.update(dto, id);
    }

    @PatchMapping("/{id}")
    public UserResponseDTO partialUpdateUser(
            @PathVariable Long id, @Validated(OnUpdate.class) @RequestBody UserRequestDTO dto) {

        return service.partialUpdate(dto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // ENDPOINTS HISTÓRICOS (Separados para proteger el flujo principal)
    @GetMapping("/history")
    public List<UserResponseDTO> getHistoricalUsers() {

        return service.getHistoricalUsers();
    }

    @GetMapping("/{id}/history")
    public UserResponseDTO findHistoricalUser(@PathVariable Long id) {

		return service.findHistoricalUser(id);
    }
}
