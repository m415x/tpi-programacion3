package ar.edu.tup.programacion3.SGP.repository;

import ar.edu.tup.programacion3.SGP.model.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends BaseRepository<Category, UUID> {

    List<Category> findByNameContainingIgnoreCase(String name);

	// CONSULTAS HISTÓRICAS (Nativas para evadir @SQLRestriction)
	@Query(value = "SELECT * FROM categories WHERE deleted = true", nativeQuery = true)
	List<Category> findDeletedAll();

	@Query(value = "SELECT * FROM categories WHERE id = :id AND deleted = true", nativeQuery = true)
	Optional<Category> findDeletedById(@Param("id") UUID id);

	default Category findDeletedByIdOrThrow(UUID id) {
		return findDeletedById(id)
				.orElseThrow(() -> new EntityNotFoundException(
						"No se encontró ningún registro histórico de la categoría con ID: " + id));
	}
}
