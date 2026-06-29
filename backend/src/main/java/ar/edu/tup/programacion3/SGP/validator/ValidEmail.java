package ar.edu.tup.programacion3.SGP.validator;

import ar.edu.tup.programacion3.SGP.validator.groups.OnCreate;
import ar.edu.tup.programacion3.SGP.validator.groups.OnUpdate;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.lang.annotation.*;

@NotBlank(message = "El email es obligatorio", groups = OnCreate.class)
@Email(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
		message = "error de formato de email",
        groups = {OnCreate.class, OnUpdate.class})
@Size(
        min = 6,
        max = 50,
        message = "El email debe tener entre 6 y 50 caracteres",
        groups = {OnCreate.class, OnUpdate.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidEmail {

    String message() default "El campo provisto no es válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
