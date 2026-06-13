package ar.edu.tup.programacion3.SistemaGestionPedidos.validator;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.groups.OnCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.groups.OnUpdate;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.annotation.*;

@NotBlank(message = "La contraseña es obligatoria", groups = OnCreate.class)
@Size(
        min = 6,
        max = 64,
        message = "La contraseña debe tener entre 6 y 64 caracteres",
        groups = {OnCreate.class, OnUpdate.class})
@Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,64}$",
        message =
                "La contraseña debe tener entre 6 y 64 caracteres, al menos una letra mayúscula, una minúscula, un "
                        + "número y un carácter especial",
        groups = {OnCreate.class, OnUpdate.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidPassword {

    String message() default "El campo provisto no es válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
