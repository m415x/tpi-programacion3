package ar.edu.tup.programacion3.SGP.dto;

import ar.edu.tup.programacion3.SGP.model.enums.UserRole;
import java.util.Set;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        UserRole userRole,
        Set<OrderResponseDTO> orders) {}
