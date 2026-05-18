package ar.edu.tup.programacion3.SistemaGestionPedidos.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.*;

@NotNull(message = "{message} no puede ser nulo")
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidNotNull {

    String message() default "El estado";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
