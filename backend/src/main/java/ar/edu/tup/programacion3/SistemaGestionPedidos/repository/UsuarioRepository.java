package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String name, String apellido);

    Optional<Usuario> findByEmailIgnoreCase(String email);

	@Query("SELECT u FROM Usuario u JOIN u.pedidos p GROUP BY u.id ORDER BY COUNT(p) DESC LIMIT 1")
    Optional<Usuario> getUserWithMoreOrders();

    @Query(value = "SELECT * FROM users WHERE id = :id", nativeQuery = true)
    Optional<Usuario> findByIdIncludingDeleted(@Param("id") Long id);

    @Query(value = "SELECT * FROM users", nativeQuery = true)
    List<Usuario> findAllIncludingDeleted();
}
