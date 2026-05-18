package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT u.id FROM Usuario u JOIN u.pedidos p WHERE p.id = :pedidoId")
    Optional<Long> findUsuarioIdByPedidoId(@Param("pedidoId") Long pedidoId);

    @Query("SELECT COALESCE(SUM(d.cantidad), 0) FROM Pedido p JOIN p.detallePedido d WHERE p.id = :pedidoId")
    Long getQtyItems(@Param("pedidoId") Long pedidoId);

	@Query(value = "SELECT * FROM orders WHERE id = :id", nativeQuery = true)
	Optional<Pedido> findByIdIncludingDeleted(@Param("id") Long id);

	@Query(value = "SELECT * FROM orders", nativeQuery = true)
	List<Pedido> findAllIncludingDeleted();
}
