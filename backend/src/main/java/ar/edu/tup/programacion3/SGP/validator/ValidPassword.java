package ar.edu.tup.programacion3.SGP.validator;

import ar.edu.tup.programacion3.SGP.validator.groups.OnCreate;
import ar.edu.tup.programacion3.SGP.validator.groups.OnUpdate;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.annotation.*;

@NotBlank(message = "La contraseña es obligatoria", groups = OnCreate.class)
@Size(
		min = 60,
		max = 60,
		message = "La contraseña encriptada con BCrypt debe tener exactamente 60 caracteres",
		groups = {OnCreate.class, OnUpdate.class})
@Pattern(
		regexp = "^\\$2[ayb]\\$\\d{2}\\$[./A-Za-z0-9]{53}$",
		message = "La contraseña provista no posee un formato de encriptación BCrypt válido",
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
