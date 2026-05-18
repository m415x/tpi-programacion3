package ar.edu.tup.programacion3.SistemaGestionPedidos.entity;

import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.Rol;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidEmail;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidName;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidPassword;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidPhone;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "users")
@SQLRestriction("deleted = false")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario extends Base {

    @Column(name = "first_name", nullable = false, length = 50)
    @ValidName
    private String nombre;

    @Column(name = "last_name", nullable = false, length = 50)
    @ValidName(message = "apellido")
    private String apellido;

    @Column(name = "email", unique = true, nullable = false, length = 50)
    @ValidEmail
    private String email;

    @Column(name = "phone", length = 20)
    @ValidPhone
    private String telefono;

    @Column(name = "password", nullable = false, length = 64)
    @ValidPassword
    @ToString.Exclude
    private String contraseña;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Rol rol = Rol.USUARIO;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Setter(AccessLevel.NONE)
    @Builder.Default
    @ToString.Exclude
    private Set<Pedido> pedidos = new HashSet<>();

    public void addPedido(Pedido pedido) {
        this.pedidos.add(pedido);
    }
}