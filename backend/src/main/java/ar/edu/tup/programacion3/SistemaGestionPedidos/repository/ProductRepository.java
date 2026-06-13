package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Product;
import java.util.List;
import java.util.Optional;

import ar.edu.tup.programacion3.SistemaGestionPedidos.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends BaseRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByAvailable(Boolean available);

    List<Product> findByStockLessThan(Integer limit);

    @Query("SELECT c.id FROM Category c JOIN c.products p WHERE p.id = :productId")
    Optional<Long> findCategoryIdByProductId(@Param("productId") Long productId);

    @Query("SELECT p FROM Category c JOIN c.products p WHERE c.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

	// CONSULTAS HISTÓRICAS (Nativas para evadir @SQLRestriction)
	@Query(value = "SELECT * FROM products WHERE deleted = true", nativeQuery = true)
	List<Product> findDeletedAll();

	@Query(value = "SELECT * FROM products WHERE id = :id AND deleted = true", nativeQuery = true)
	Optional<Product> findDeletedById(@Param("id") Long id);

	default Product findDeletedByIdOrThrow(Long id) {
		return findDeletedById(id)
				.orElseThrow(() -> new EntityNotFoundException(
						"No se encontró ningún registro histórico del producto con ID: " + id));
	}
}
