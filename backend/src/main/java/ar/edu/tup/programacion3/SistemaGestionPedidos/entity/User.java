package ar.edu.tup.programacion3.SistemaGestionPedidos.entity;

import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.UserRole;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.*;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

/**
 * Entidad que representa a un usuario del sistema. Esta clase extiende de {@link Base} y contiene
 * la información personal, credenciales de acceso y el historial de orders asociados al usuario.
 */
@Entity
@Table(
        name = "users",
        // Índice individual para la autenticación por email
        indexes = {@Index(name = "idx_user_email", columnList = "email")})
@SQLRestriction("deleted = false")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Base {

    @Column(name = "first_name", nullable = false, length = 50)
    @ValidName
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    @ValidName(message = "lastName")
    private String lastName;

    @Column(name = "email", unique = true, nullable = false, length = 50)
    @ValidEmail
    private String email;

    @Column(name = "phone", length = 20)
    @ValidPhone
    private String phone;

    @Column(name = "password", nullable = false, length = 64)
    @ValidPassword
    @ToString.Exclude
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole userRole = UserRole.CLIENT;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Setter(AccessLevel.NONE)
    @Builder.Default
    @ToString.Exclude
    private Set<Order> orders = new HashSet<>();

    public void addOrder(Order order) {
        this.orders.add(order);
    }
}
