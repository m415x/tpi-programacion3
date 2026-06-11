package ar.edu.tup.programacion3.SistemaGestionPedidos.dto;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidEmail;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidName;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidPassword;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidPhone;

public record UserRequestDTO(
        @ValidName String firstName,
        @ValidName String lastName,
        @ValidEmail String email,
        @ValidPhone String phone,
        @ValidPassword String password) {}
