package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("SELECT u FROM User u JOIN u.orders p GROUP BY u.id ORDER BY COUNT(p) DESC LIMIT 1")
    Optional<User> getUserWithMoreOrders();
}
