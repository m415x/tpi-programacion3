package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.OrderDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends BaseRepository<OrderDetail, Long> {

    @Query("SELECT p.id FROM Order p JOIN p.orderDetails d WHERE d.id = :orderDetailId")
    Optional<Long> findOrderIdByOrderDetailId(@Param("orderDetailId") Long orderDetailId);

    @Query("SELECT c.id FROM Category c JOIN c.products p WHERE p.id = :productId")
    Optional<Long> findCategoryIdByProductId(@Param("productId") Long productId);

    @Query(value = "SELECT * FROM order_details WHERE id = :id", nativeQuery = true)
    Optional<OrderDetail> findByIdIncludingDeleted(@Param("id") Long id);

    @Query(value = "SELECT * FROM order_details", nativeQuery = true)
    List<OrderDetail> findAllIncludingDeleted();
}
