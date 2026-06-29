package ar.edu.tup.programacion3.SGP.exception;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ar.edu.tup.programacion3.SGP.dto.ErrorResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controlador global de excepciones para la aplicación.
 * Proporciona métodos para manejar excepciones específicas
 * de la aplicación y devolver respuestas de error estandarizadas.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	// ResourceNotFoundException y EntityNotFoundException retornan 404
	@ExceptionHandler({ResourceNotFoundException.class, EntityNotFoundException.class})
	public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(
			RuntimeException ex, HttpServletRequest request) {

		ErrorResponseDTO errorResponseDto = ErrorResponseDTO.simpleOf(
				HttpStatus.NOT_FOUND.value(),
				"Recurso no encontrado",
				ex.getMessage(),
				request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
	}

	// BusinessException e IllegalArgumentException retornan 400
	@ExceptionHandler({BusinessException.class, IllegalArgumentException.class, UnsupportedOperationException.class})
	public ResponseEntity<ErrorResponseDTO> handleBusiness(
			RuntimeException ex, HttpServletRequest request) {

		ErrorResponseDTO errorResponseDto = ErrorResponseDTO.simpleOf(
				HttpStatus.BAD_REQUEST.value(),
				"Operación o argumento inválido",
				ex.getMessage(),
				request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
	}

	// Validación MethodArgumentNotValidException retorna 400 con detalles de campos
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponseDTO> handleValidationErrors(
			MethodArgumentNotValidException ex, HttpServletRequest request) {

		// Recolectamos la lista de strings con los fallos de @NotBlank, @Size, etc.
		List<String> details = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.toList());

		ErrorResponseDTO errorResponseDto = ErrorResponseDTO.of(
				HttpStatus.BAD_REQUEST.value(),
				"Error de validación",
				details,
				request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
	}

	// Maneja errores de violaciones de unicidad o llaves foráneas en DB
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(
			DataIntegrityViolationException ex, HttpServletRequest request) {

		String detalleMensaje = "El recurso que intenta registrar ya existe o rompe una regla de unicidad.";

		if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("email")) {
			detalleMensaje = "El correo electrónico ya se encuentra registrado en el sistema.";
		} else if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("users")) {
			detalleMensaje = "Ya existe un usuario con esos datos duplicados.";
		}

		ErrorResponseDTO errorResponseDto = ErrorResponseDTO.simpleOf(
				HttpStatus.CONFLICT.value(), // 409 Conflict
				"Conflicto de datos",
				detalleMensaje,
				request.getRequestURI());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDto);
	}

	// Exception genérica retorna 500 y escribe la traza en el log de Spring Boot
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex, HttpServletRequest request) {

		// Satisface el requisito de logear la traza real del error en la consola del servidor
		log.error("Excepción interna no controlada capturada en el perímetro REST: ", ex);

		ErrorResponseDTO errorResponseDto = ErrorResponseDTO.simpleOf(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Error interno del servidor",
				"Ocurrió un inconveniente inesperado: " + ex.getMessage(),
				request.getRequestURI());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
	}

    // Maneja errores de validación de anotaciones como @NotNull, @Size, etc. en entidades o DTOs
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        List<String> details =
                ex.getConstraintViolations().stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .toList();

        ErrorResponseDTO errorResponseDto =
                ErrorResponseDTO.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Error de validación",
                        details,
                        request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    // Maneja errores de formato numérico inválido (como cuando envías un ID que no es un número)
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorResponseDTO> handleNumberFormat(
            NumberFormatException ex, HttpServletRequest request) {

        ErrorResponseDTO errorResponseDto =
                ErrorResponseDTO.simpleOf(
                        HttpStatus.BAD_REQUEST.value(),
                        "Formato numérico inválido",
                        "El parámetro enviado debe ser un número entero válido.",
                        request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }
}
