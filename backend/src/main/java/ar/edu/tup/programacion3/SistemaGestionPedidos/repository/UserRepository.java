package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("SELECT u FROM User u JOIN u.orders p GROUP BY u.id ORDER BY COUNT(p) DESC LIMIT 1")
    Optional<User> getUserWithMoreOrders();

	// CONSULTAS HISTÓRICAS (Nativas para evadir @SQLRestriction)
	@Query(value = "SELECT * FROM users WHERE deleted = true", nativeQuery = true)
	List<User> findDeletedAll();

	@Query(value = "SELECT * FROM users WHERE id = :id AND deleted = true", nativeQuery = true)
	Optional<User> findDeletedById(@Param("id") Long id);

	default User findDeletedByIdOrThrow(Long id) {
		return findDeletedById(id)
				.orElseThrow(() -> new EntityNotFoundException(
						"No se encontró ningún registro histórico del usuario con ID: " + id));
	}
}
