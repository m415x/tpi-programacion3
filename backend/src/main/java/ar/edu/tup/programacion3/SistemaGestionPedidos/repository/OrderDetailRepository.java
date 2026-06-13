package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.model.OrderDetail;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends BaseRepository<OrderDetail, Long> {

    @Query("SELECT p.id FROM Order p JOIN p.orderDetails d WHERE d.id = :orderDetailId")
    Optional<Long> findOrderIdByOrderDetailId(@Param("orderDetailId") Long orderDetailId);

    @Query("SELECT c.id FROM Category c JOIN c.products p WHERE p.id = :productId")
    Optional<Long> findCategoryIdByProductId(@Param("productId") Long productId);

	// CONSULTAS HISTÓRICAS (Nativas para evadir @SQLRestriction)
	@Query(value = "SELECT * FROM order_details WHERE deleted = true", nativeQuery = true)
	List<OrderDetail> findDeletedAll();

	@Query(value = "SELECT * FROM order_details WHERE id = :id AND deleted = true", nativeQuery = true)
	Optional<OrderDetail> findDeletedById(@Param("id") Long id);

	default OrderDetail findDeletedByIdOrThrow(Long id) {
		return findDeletedById(id)
				.orElseThrow(() -> new EntityNotFoundException(
						"No se encontró ningún registro histórico del usuario con ID: " + id));
	}
}
