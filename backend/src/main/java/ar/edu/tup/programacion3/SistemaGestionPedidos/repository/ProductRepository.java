package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends BaseRepository<Product, Long> {

    List<Product> findByAvailable(Boolean available);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByStockLessThan(Integer limit);

    @Query("SELECT c.id FROM Category c JOIN c.products p WHERE p.id = :productId")
    Optional<Long> findCategoryIdByProductId(@Param("productId") Long productId);
}
