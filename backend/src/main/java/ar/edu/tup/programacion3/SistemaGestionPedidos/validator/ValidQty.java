package ar.edu.tup.programacion3.SistemaGestionPedidos.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.*;
import java.lang.annotation.*;

@NotNull(message = "El campo no puede ser nulo")
@Min(value = 0, message = "El campo no puede ser negativo")
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidQty {

	String message() default "El campo provisto no es válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
