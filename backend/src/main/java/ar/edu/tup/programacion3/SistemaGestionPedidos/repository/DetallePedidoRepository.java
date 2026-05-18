package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

	@Query("SELECT p.id FROM Pedido p JOIN p.detallePedido d WHERE d.id = :detalleId")
	Optional<Long> findPedidoIdByDetalleId(@Param("detalleId") Long detalleId);

	@Query("SELECT c.id FROM Categoria c JOIN c.productos p WHERE p.id = :productoId")
	Optional<Long> findCategoriaIdByProductoId(@Param("productoId") Long productoId);

    @Query(value = "SELECT * FROM order_items WHERE id = :id", nativeQuery = true)
    Optional<DetallePedido> findByIdIncludingDeleted(@Param("id") Long id);

    @Query(value = "SELECT * FROM order_items", nativeQuery = true)
    List<DetallePedido> findAllIncludingDeleted();
}
