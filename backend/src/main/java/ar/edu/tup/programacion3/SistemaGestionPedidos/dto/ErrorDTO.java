package ar.edu.tup.programacion3.SistemaGestionPedidos.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorDTO(
        LocalDateTime timestamp, int status, String error, List<String> details, String path) {
    public static ErrorDTO of(int status, String error, List<String> details, String path) {
        return new ErrorDTO(LocalDateTime.now(), status, error, details, path);
    }

    public static ErrorDTO simpleOf(int status, String error, String detail, String path) {
        return new ErrorDTO(LocalDateTime.now(), status, error, List.of(detail), path);
    }
}
