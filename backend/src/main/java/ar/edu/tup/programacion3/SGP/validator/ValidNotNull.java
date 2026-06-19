package ar.edu.tup.programacion3.SGP.validator;

import ar.edu.tup.programacion3.SGP.validator.groups.OnCreate;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.*;

@NotNull(message = "El campo no puede ser nulo", groups = OnCreate.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidNotNull {

    String message() default "El campo provisto no es válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
