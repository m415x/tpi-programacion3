package ar.edu.tup.programacion3.SistemaGestionPedidos.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.lang.annotation.*;

@NotBlank(message = "El {message} no puede ser nulo o vacío")
@Size(min = 2, max = 50, message = "El {message} debe tener entre 2 y 50 caracteres")
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidName {

    String message() default "nombre";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
