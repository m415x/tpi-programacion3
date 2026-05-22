package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Base;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends Base, ID> extends JpaRepository<T, ID> {

    // Trae la entidad por ID o arroja una excepción
    default T findByIdOrThrow(ID id) {

        return findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Recurso no encontrado con ID: " + id));
    }

    // CONSULTAS DE AUDITORÍA (Ignoran el soft delete)
    // Métodos abstractos que Spring Data JPA implementa automáticamente por convención de nombres
    Optional<T> findWithDeletedById(ID id);

    List<T> findWithDeletedBy();

    // Métodos default con la lógica limpia
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
