package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Base;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T extends Base, ID> extends JpaRepository<T, ID> {

    // Búsqueda estándar con manejo de excepciones
    default T findByIdOrThrow(ID id) {

        return findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Recurso no encontrado con ID: " + id));
    }
}
