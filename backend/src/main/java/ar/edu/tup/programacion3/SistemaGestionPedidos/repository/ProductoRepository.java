package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByDisponible(Boolean available);

    List<Producto> findByNombreContainingIgnoreCase(String name);

    List<Producto> findByStockLessThan(Integer limit);

    @Query("SELECT c.id FROM Categoria c JOIN c.productos p WHERE p.id = :productoId")
    Optional<Long> findCategoryIdByProductId(@Param("productoId") Long productoId);

    @Query(value = "SELECT * FROM products WHERE id = :id", nativeQuery = true)
    Optional<Producto> findByIdIncludingDeleted(@Param("id") Long id);

    @Query(value = "SELECT * FROM products", nativeQuery = true)
    List<Producto> findAllIncludingDeleted();
}
