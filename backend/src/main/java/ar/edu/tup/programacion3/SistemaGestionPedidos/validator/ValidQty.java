package ar.edu.tup.programacion3.SistemaGestionPedidos.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.*;

import java.lang.annotation.*;

@NotNull(message = "{message} no puede ser nulo")
@Min(value = 0, message = "{message} no puede ser negativo")
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidQty {

    String message() default "La cantidad";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
