package ar.edu.tup.programacion3.SGP.model;

import ar.edu.tup.programacion3.SGP.validator.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "order_details")
@SQLRestriction("deleted = false")
@DynamicUpdate
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail extends Base {

    @Column(name = "quantity", nullable = false)
    @ValidQty
    @Setter(AccessLevel.NONE)
    private Integer quantity;

    @Column(name = "subtotal", nullable = false)
    @ValidAmount(message = "subtotal")
    @Setter(AccessLevel.NONE)
    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @ValidNotNull(message = "El product")
    @Setter(AccessLevel.NONE)
    private Product product;

    public OrderDetail(Integer qty, Product product) {

        super();
        this.quantity = qty;
        this.product = product;
        this.subtotal = getCalculatedSubtotal();
    }

    public static OrderDetail of(Integer qty, Product product) {

        return OrderDetail.builder().quantity(qty).product(product).build().recalculate();
    }

    public void setQty(Integer qty) {

        this.quantity = qty;
        recalculate();
    }

    public void setProduct(Product product) {

        this.product = product;
        recalculate();
    }

    public BigDecimal getCalculatedSubtotal() {

        return Optional.ofNullable(product)
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .orElse(BigDecimal.ZERO);
    }

    public OrderDetail recalculate() {

        this.subtotal = getCalculatedSubtotal();
        return this;
    }
}
