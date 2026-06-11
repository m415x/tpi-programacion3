package ar.edu.tup.programacion3.SistemaGestionPedidos.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.*;

@NotNull(message = "El monto no puede ser nulo")
@DecimalMin(value = "0.0", message = "El monto debe ser mayor o igual a cero")
@Digits(integer = 10, fraction = 2, message = "El monto no tiene un formato válido")
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidAmount {

	String message() default "El campo provisto no es válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
