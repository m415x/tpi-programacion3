package ar.edu.tup.programacion3.SistemaGestionPedidos.entity;

import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.Estado;
import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.FormaPago;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidAmount;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidNotNull;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "orders")
@SQLRestriction("deleted = false")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido extends Base implements Calculable {

    @Column(name = "date", nullable = false)
    @ValidNotNull(message = "La fecha")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Builder.Default
    protected LocalDate fecha = LocalDate.now();

    @Column(name = "status")
    @ValidNotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Estado estado = Estado.PENDIENTE;

    @Column(name = "total", nullable = false)
    @ValidAmount(message = "total")
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "payment_method")
    @ValidNotNull(message = "La forma de pago")
    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    @Setter(AccessLevel.NONE)
    @Builder.Default
    @ToString.Exclude
    private Set<DetallePedido> detallePedido = new HashSet<>();

    @PreUpdate
    protected void onUpdate() {
        this.fecha = LocalDate.now();
    }

    public Pedido(FormaPago formaPago, Set<DetallePedido> detallePedido) {

        this.formaPago = formaPago;
        this.detallePedido = detallePedido;

        calcularTotal();
    }

    @Override
    public void calcularTotal() {

        this.total =
                detallePedido.stream()
                        .map(DetallePedido::getSubtotal)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addDetallePedido(int cantidad, Producto producto) {

        findeDetallePedidoByProducto(producto)
                .ifPresentOrElse(
                        existente -> existente.setCantidad(existente.getCantidad() + cantidad),
                        () -> this.detallePedido.add(DetallePedido.of(cantidad, producto)));

        calcularTotal();
    }

    public void updateQtyProducto(Producto producto, int nuevaCantidad) {

        if (nuevaCantidad <= 0) {
            deleteDetallePedidoByProducto(producto);
        } else {
            findeDetallePedidoByProducto(producto)
                    .ifPresent(
                            existente ->
                                    existente.setCantidad(
                                            nuevaCantidad)); // Sobreescribe el valor absoluto
        }

        calcularTotal();
    }

    public Optional<DetallePedido> findeDetallePedidoByProducto(Producto producto) {

        return detallePedido.stream()
                .filter(detalle -> detalle.getProducto().equals(producto))
                .findFirst();
    }

    public void deleteDetallePedidoByProducto(Producto producto) {

        findeDetallePedidoByProducto(producto)
                .ifPresent(
                        detalle -> {
                            this.detallePedido.remove(detalle);

                            calcularTotal();
                        });
    }
}
