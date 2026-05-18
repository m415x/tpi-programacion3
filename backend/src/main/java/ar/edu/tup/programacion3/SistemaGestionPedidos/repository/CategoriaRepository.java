package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByNombreContainingIgnoreCase(String name);

    @Query(value = "SELECT * FROM categories WHERE id = :id", nativeQuery = true)
    Optional<Categoria> findByIdIncludingDeleted(@Param("id") Long id);

    @Query(value = "SELECT * FROM categories", nativeQuery = true)
    List<Categoria> findAllIncludingDeleted();
}
