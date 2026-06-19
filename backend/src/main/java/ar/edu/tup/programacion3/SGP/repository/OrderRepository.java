package ar.edu.tup.programacion3.SGP.repository;

import ar.edu.tup.programacion3.SGP.model.Order;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends BaseRepository<Order, UUID> {

    @Query("SELECT u.id FROM User u JOIN u.orders p WHERE p.id = :orderId")
    Optional<UUID> findUserIdByOrderId(@Param("orderId") UUID orderId);

    @Query(
            "SELECT COALESCE(SUM(d.quantity), 0) FROM Order p JOIN p.orderDetails d WHERE p.id = :orderId")
    UUID getQtyItems(@Param("orderId") UUID orderId);

	// CONSULTAS HISTÓRICAS (Nativas para evadir @SQLRestriction)
	@Query(value = "SELECT * FROM orders WHERE deleted = true", nativeQuery = true)
	List<Order> findDeletedAll();

	@Query(value = "SELECT * FROM orders WHERE id = :id AND deleted = true", nativeQuery = true)
	Optional<Order> findDeletedById(@Param("id") UUID id);

	default Order findDeletedByIdOrThrow(UUID id) {
		return findDeletedById(id)
				.orElseThrow(() -> new EntityNotFoundException(
						"No se encontró ningún registro histórico del pedido con ID: " + id));
	}
}
