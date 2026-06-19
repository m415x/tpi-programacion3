package ar.edu.tup.programacion3.SGP.dto;

import ar.edu.tup.programacion3.SGP.validator.groups.OnCreate;
import ar.edu.tup.programacion3.SGP.validator.ValidNotNull;
import ar.edu.tup.programacion3.SGP.validator.ValidQty;

import java.util.UUID;

public record OrderDetailRequestDTO(
        @ValidQty Integer quantity,
        @ValidNotNull(groups = OnCreate.class) UUID productId,
        UUID orderId) {}
