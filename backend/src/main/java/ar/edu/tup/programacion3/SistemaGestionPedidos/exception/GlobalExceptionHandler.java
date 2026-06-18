package ar.edu.tup.programacion3.SistemaGestionPedidos.exception;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ErrorDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Maneja errores de validación de @Valid en los controladores
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

    // Maneja errores de validación de anotaciones como @NotNull, @Size, etc. en entidades o DTOs
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

    // Maneja errores de argumentos inválidos en métodos de servicio o controladores (como IllegalArgumentException)
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

    // Maneja errores de recursos no encontrados (como cuando buscas un pedido que no existe)
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

    // Maneja errores de formato numérico inválido (como cuando envías un ID que no es un número)
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

    // Maneja errores de operaciones no permitidas
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ErrorDTO> handleIllegalOperations(
		    UnsupportedOperationException ex, HttpServletRequest request) {

	    // Centralizamos el mapeo directo a un HTTP 400 Bad Request
	    ErrorDTO errorResponse = ErrorDTO.simpleOf(
			    HttpStatus.BAD_REQUEST.value(),
			    "Operación no soportada o prohibida",
			    ex.getMessage(),
			    request.getRequestURI()
	    );

	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Maneja errores de violación de integridad de datos (intentar insertar un email duplicado)
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

    // Maneja cualquier otro error no capturado por los manejadores anteriores (error genérico)
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
