package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidLongText;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidName;

public record CategoryEdit(@ValidName String name, @ValidLongText String description) {}
