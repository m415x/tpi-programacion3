package ar.edu.tup.programacion3.SistemaGestionPedidos.entity;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidAmount;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidNotNull;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidQty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Optional;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "order_items")
@SQLRestriction("deleted = false")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido extends Base {

    @Column(name = "quantity", nullable = false)
    @ValidQty
    @Setter(AccessLevel.NONE)
    private Integer cantidad;

    @Column(name = "subtotal", nullable = false)
    @ValidAmount(message = "subtotal")
    @Setter(AccessLevel.NONE)
    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @ValidNotNull(message = "El producto")
    @Setter(AccessLevel.NONE)
    private Producto producto;

    public DetallePedido(Integer cantidad, Producto producto) {
        super();
        this.cantidad = cantidad;
        this.producto = producto;
        this.subtotal = getSubtotalCalculado();
    }

    public static DetallePedido of(Integer cantidad, Producto producto) {
        return DetallePedido.builder().cantidad(cantidad).producto(producto).build().recalcular();
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        recalcular();
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        recalcular();
    }

    public BigDecimal getSubtotalCalculado() {
        return Optional.ofNullable(producto)
                .map(p -> p.getPrecio().multiply(BigDecimal.valueOf(cantidad)))
                .orElse(BigDecimal.ZERO);
    }

    public DetallePedido recalcular() {
        this.subtotal = getSubtotalCalculado();
        return this;
    }
}
