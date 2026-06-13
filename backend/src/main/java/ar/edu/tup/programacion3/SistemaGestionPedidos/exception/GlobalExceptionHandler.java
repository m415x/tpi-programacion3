package ar.edu.tup.programacion3.SistemaGestionPedidos.exception;

import ar.edu.tup.programacion3.SistemaGestionPedidos.ConsoleMenuRunner;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ErrorDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Captura los errores de validación de los DTOs (como el 400 Bad Request que te salía antes)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> details =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList());

        ErrorDTO errorResponse =
                ErrorDTO.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Error de validación",
                        details,
                        request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Maneja errores de ConstraintViolation (en otros contextos)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDTO> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        List<String> details =
                ex.getConstraintViolations().stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .toList();

        ErrorDTO errorResponse =
                ErrorDTO.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Error de validación",
                        details,
                        request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Maneja IllegalArgumentException (por ejemplo en validaciones manuales)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        ErrorDTO errorResponse =
                ErrorDTO.simpleOf(
                        HttpStatus.BAD_REQUEST.value(),
                        "Argumento inválido",
                        ex.getMessage(),
                        request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Cuando no se encuentra una entidad en la base de datos
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleEntityNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {

        ErrorDTO errorResponse =
                ErrorDTO.simpleOf(
                        HttpStatus.NOT_FOUND.value(),
                        "Recurso no encontrado",
                        ex.getMessage(),
                        request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Cuando se manda una letra en vez de un número
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorDTO> handleNumberFormat(
            NumberFormatException ex, HttpServletRequest request) {

        ErrorDTO errorResponse =
                ErrorDTO.simpleOf(
                        HttpStatus.BAD_REQUEST.value(),
                        "Formato numérico inválido",
                        "El parámetro enviado debe ser un número entero válido.",
                        request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

	@ExceptionHandler({
			UnsupportedOperationException.class,
			ConsoleMenuRunner.OperationCancelledException.class
	})
	public ResponseEntity<ErrorDTO> handleIllegalOperations(
			Exception ex, HttpServletRequest request) {

		// Si la excepción viene de tus bloqueos en OrderDetail, mandamos un HTTP 400 Bad Request
		if (ex instanceof UnsupportedOperationException) {
			ErrorDTO errorResponse = ErrorDTO.simpleOf(
					HttpStatus.BAD_REQUEST.value(),
					"Operación no soportada o prohibida",
					ex.getMessage(),
					request.getRequestURI()
			);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}

		// Si viene del cancelado de consola, mantiene el HTTP 409 Conflict original
		ErrorDTO errorResponse = ErrorDTO.simpleOf(
				HttpStatus.CONFLICT.value(),
				"Operación no permitida",
				ex.getMessage(),
				request.getRequestURI());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDTO> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        String detalleMensaje =
                "El recurso que intenta registrar ya existe o rompe una regla de unicidad.";

        // Opcional: Un análisis simple para dar un mensaje más amigable si es el email
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("email")) {
            detalleMensaje = "El correo electrónico ya se encuentra registrado en el sistema.";
        } else if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("users")) {
            detalleMensaje = "Ya existe un usuario con esos datos duplicados.";
        }

        ErrorDTO errorResponse =
                ErrorDTO.simpleOf(
                        HttpStatus.CONFLICT.value(), // Código 409
                        "Conflicto de datos",
                        detalleMensaje,
                        request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // Captura excepciones genéricas del sistema (por si explota algo inesperado)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGeneric(Exception ex, HttpServletRequest request) {

        ErrorDTO errorResponse =
                ErrorDTO.simpleOf(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error interno del servidor",
                        ex.getMessage(),
                        request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
