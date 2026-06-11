package ar.edu.tup.programacion3.SistemaGestionPedidos.dto;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidLongText;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidName;

public record CategoryRequestDTO(@ValidName String name, @ValidLongText String description) {}
