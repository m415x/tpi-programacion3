package ar.edu.tup.programacion3.SGP.dto;

import ar.edu.tup.programacion3.SGP.validator.ValidLongText;
import ar.edu.tup.programacion3.SGP.validator.ValidName;

public record CategoryRequestDTO(
        @ValidName String name, @ValidLongText String description, @ValidLongText String image) {}
