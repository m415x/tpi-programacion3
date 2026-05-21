package ar.edu.tup.programacion3.SistemaGestionPedidos.dto.user;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidEmail;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidName;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidPassword;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidPhone;

public record UserEdit(
        @ValidName String firstName,
        @ValidName(message = "apellido") String lastName,
        @ValidEmail String email,
        @ValidPhone String phone,
        @ValidPassword String password) {}
