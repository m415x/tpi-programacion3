package ar.edu.tup.programacion3.SistemaGestionPedidos.dto;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.groups.OnCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidNotNull;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidQty;

public record OrderDetailRequestDTO(
        @ValidQty Integer quantity,
        @ValidNotNull(groups = OnCreate.class) Long productId,
        Long orderId) {}
