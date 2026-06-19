package ar.edu.tup.programacion3.SGP.dto;

import ar.edu.tup.programacion3.SGP.validator.ValidEmail;
import ar.edu.tup.programacion3.SGP.validator.ValidName;
import ar.edu.tup.programacion3.SGP.validator.ValidPassword;
import ar.edu.tup.programacion3.SGP.validator.ValidPhone;

public record UserRequestDTO(
        @ValidName String firstName,
        @ValidName String lastName,
        @ValidEmail String email,
        @ValidPhone String phone,
        @ValidPassword String password) {}
