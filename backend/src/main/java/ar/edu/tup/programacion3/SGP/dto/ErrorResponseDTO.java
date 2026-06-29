package ar.edu.tup.programacion3.SGP.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO(
        LocalDateTime timestamp, int status, String error, List<String> details, String path) {
    public static ErrorResponseDTO of(int status, String error, List<String> details, String path) {
        return new ErrorResponseDTO(LocalDateTime.now(), status, error, details, path);
    }

    public static ErrorResponseDTO simpleOf(int status, String error, String detail, String path) {
        return new ErrorResponseDTO(LocalDateTime.now(), status, error, List.of(detail), path);
    }
}
