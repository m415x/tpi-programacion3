package ar.edu.tup.programacion3.SGP.controller;

import ar.edu.tup.programacion3.SGP.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.UserResponseDTO;
import ar.edu.tup.programacion3.SGP.service.UserService;
import ar.edu.tup.programacion3.SGP.validator.AdminRequired;
import ar.edu.tup.programacion3.SGP.validator.groups.OnCreate;
import ar.edu.tup.programacion3.SGP.validator.groups.OnUpdate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponseDTO> saveUser(
            @Validated(OnCreate.class) @RequestBody UserRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
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

        // Acoplamos el verificador de BCrypt del componente PasswordEncoder
        if (user != null && service.verifyCredentials(email, password)) {

            return ResponseEntity.ok(user);
        }

        // Si las credenciales fallan, devolvemos HTTP 401 Unauthorized
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{id}")
    @AdminRequired
    public ResponseEntity<UserResponseDTO> findUser(@PathVariable UUID id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @AdminRequired
    public ResponseEntity<List<UserResponseDTO>> findUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {

        if (email != null && !email.trim().isEmpty()) {
            UserResponseDTO user = service.findUserByEmail(email);

            List<UserResponseDTO> result = (user != null) ? List.of(user) : List.of();
            return ResponseEntity.ok(result);
        }

        if (name != null && !name.trim().isEmpty()) {

            return ResponseEntity.ok(service.findUsersByName(name));
        }

        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/{id}")
    @AdminRequired
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable UUID id, @Valid @RequestBody UserRequestDTO dto) {

        return ResponseEntity.ok(service.update(dto, id));
    }

    @PatchMapping("/{id}")
    @AdminRequired
    public ResponseEntity<UserResponseDTO> partialUpdateUser(
            @PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody UserRequestDTO dto) {

        return ResponseEntity.ok(service.partialUpdate(dto, id));
    }

    @DeleteMapping("/{id}")
    @AdminRequired
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top-customer")
    @AdminRequired
    public ResponseEntity<UserResponseDTO> getTopCustomer() {
        try {
            UserResponseDTO topUser = service.getUserWithMoreOrders();

            if (topUser == null) {
                // Si no hay órdenes históricas aún, devolvemos un estado vacío exitoso (204) en vez
                // de romper con 400
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(topUser);

        } catch (Exception e) {
            // Previene caídas bruscas del servidor
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getMyProfile(@RequestHeader("X-User-Id") UUID userId) {

        return ResponseEntity.ok(service.findById(userId));
    }

    @PatchMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateMyProfile(
            @RequestHeader("X-User-Id") UUID userId,
            @Validated(OnUpdate.class) @RequestBody UserRequestDTO dto) {

        return ResponseEntity.ok(service.partialUpdate(dto, userId));
    }

    // ENDPOINTS HISTÓRICOS (Separados para proteger el flujo principal)
    @GetMapping("/history")
    @AdminRequired
    public ResponseEntity<List<UserResponseDTO>> getHistoricalUsers() {

        return ResponseEntity.ok(service.getHistoricalUsers());
    }

    @GetMapping("/{id}/history")
    @AdminRequired
    public ResponseEntity<UserResponseDTO> findHistoricalUser(@PathVariable UUID id) {

        return ResponseEntity.ok(service.findHistoricalUser(id));
    }
}
