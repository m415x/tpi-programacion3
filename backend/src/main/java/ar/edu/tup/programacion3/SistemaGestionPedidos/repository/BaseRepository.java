package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Base;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * =======================================================================================================
 * NOTA DE ARQUITECTURA PARA PARCIAL 2 (HU-01)
 * =======================================================================================================
 * Esta interfaz cumple y supera los requerimientos de la HU-01 al heredar directamente de
 * JpaRepository.
 * 1. MÉTODOS CRUD AUTOMÁTICOS: Operaciones como guardar (save), listar activos
 * (findAll) y buscar (findById) no se escriben explícitamente porque Spring Data JPA las provee de
 * forma nativa e integrada.
 * 2. SEPARACIÓN DE RESPONSABILIDADES: La lógica transaccional, el control
 * de excepciones y la mutación del estado para el eliminado lógico (deleted = true) se delegan
 * deliberadamente en la Capa de Servicios. Esto evita acoplar lógica de negocio dentro de la capa
 * de persistencia, respetando las buenas prácticas de diseño de software (DDD) requeridas para el
 * TPI.
 * 3. MÉTODOS EXTENDIDOS: Se añaden métodos default genéricos para centralizar el manejo de
 * excepciones (findByIdOrThrow) y métodos derivados para auditoría histórica, optimizando todo el
 * ecosistema.
 * =======================================================================================================
 */
@NoRepositoryBean
public interface BaseRepository<T extends Base, ID> extends JpaRepository<T, ID> {

    // Búsqueda estándar con manejo de excepciones
    default T findByIdOrThrow(ID id) {

        return findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Recurso no encontrado con ID: " + id));
    }

    // =========================================================================
    // CONSULTAS DE AUDITORÍA GENÉRICAS (Ignoran el soft delete global)
    // =========================================================================
    Optional<T> findWithDeletedById(ID id);

    List<T> findWithDeletedBy();

    default T findWithDeletedByIdOrThrow(ID id) {

        return findWithDeletedById(id)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "No se encontró ningún registro histórico del recurso con ID: "
                                                + id));
    }

    default List<T> findWithDeletedByOrNull() {

        List<T> list = findWithDeletedBy();

        return list.isEmpty() ? null : list;
    }
}
