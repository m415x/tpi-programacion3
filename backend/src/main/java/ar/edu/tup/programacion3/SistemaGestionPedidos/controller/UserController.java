package ar.edu.tup.programacion3.SistemaGestionPedidos.controller;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.UserService;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.groups.OnCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.groups.OnUpdate;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public UserResponseDTO saveUser(@Validated(OnCreate.class) @RequestBody UserRequestDTO dto) {

        return service.save(dto);
    }

	@PostMapping("/login")
	public ResponseEntity<UserResponseDTO> login(@RequestBody Map<String, String> credentials) {
		String email = credentials.get("email");
		String password = credentials.get("password");

		if (email == null || password == null) {
			return ResponseEntity.badRequest().build();
		}

		// Buscamos al usuario por email en el servicio
		UserResponseDTO user = service.findUserByEmail(email);

		// Validamos existencia y comparamos el hash SHA-256 enviado por el front
		if (user != null && service.verifyCredentials(email, password)) {
			return ResponseEntity.ok(user);
		}

		// Si las credenciales fallan, devolvemos HTTP 401 Unauthorized
		return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
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
