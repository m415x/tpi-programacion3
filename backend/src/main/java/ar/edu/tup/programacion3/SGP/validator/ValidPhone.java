package ar.edu.tup.programacion3.SGP.validator;

import ar.edu.tup.programacion3.SGP.validator.groups.OnCreate;
import ar.edu.tup.programacion3.SGP.validator.groups.OnUpdate;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.*;

@Pattern(
        regexp = "^\\+?[0-9\\- ]{7,20}$",
        message = "El formato del número de teléfono es inválido",
        groups = {OnCreate.class, OnUpdate.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidPhone {

    String message() default "El campo provisto no es válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
