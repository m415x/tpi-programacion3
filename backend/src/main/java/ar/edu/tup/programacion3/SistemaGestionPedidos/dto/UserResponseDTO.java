package ar.edu.tup.programacion3.SistemaGestionPedidos.dto;

import ar.edu.tup.programacion3.SistemaGestionPedidos.model.enums.UserRole;
import java.util.Set;

public record UserResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        UserRole userRole,
        Set<OrderResponseDTO> orders) {}
