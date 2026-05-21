package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.*;
import java.math.BigDecimal;

public record ProductEdit(
        @ValidName String name,
        @ValidAmount(message = "precio") BigDecimal price,
        @ValidLongText String description,
        @ValidQty(message = "El stock") Integer stock,
        @ValidLongText(message = "La imagen") String image,
        @ValidNotNull Boolean available,
        Long categoryId) {}
