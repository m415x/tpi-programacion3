package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.user;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.order.OrderDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.UserRole;
import java.util.Set;

public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        UserRole userRole,
        Set<OrderDto> orders) {}
