package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrderRepository extends BaseRepository<Order, Long> {

    @Query("SELECT u.id FROM User u JOIN u.orders p WHERE p.id = :orderId")
    Optional<Long> findUserIdByOrderId(@Param("orderId") Long orderId);

    @Query(
            "SELECT COALESCE(SUM(d.quantity), 0) FROM Order p JOIN p.orderDetails d WHERE p.id = :orderId")
    Long getQtyItems(@Param("orderId") Long orderId);
}
