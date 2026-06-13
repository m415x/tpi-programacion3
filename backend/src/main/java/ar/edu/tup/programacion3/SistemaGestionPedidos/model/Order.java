package ar.edu.tup.programacion3.SistemaGestionPedidos.model;

import ar.edu.tup.programacion3.SistemaGestionPedidos.model.enums.OrderStatus;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.enums.PaymentMethod;
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
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(
        name = "orders",
        indexes = {
            // Índices individuales para estadísticas globales y filtros simples
            @Index(name = "idx_order_date", columnList = "date"),
            @Index(name = "idx_order_status", columnList = "order_status"),
            @Index(name = "idx_order_payment_method", columnList = "payment_method"),
            // Índice compuesto para filtro de prioridad y fecha
            @Index(name = "idx_order_status_date", columnList = "order_status, date")
        })
@SQLRestriction("deleted = false")
@DynamicUpdate
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends Base implements Calculable {

    @Column(name = "date", nullable = false)
    @ValidNotNull(message = "La fecha")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Builder.Default
    protected LocalDate date = LocalDate.now();

    @Column(name = "order_status")
    @ValidNotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(name = "total", nullable = false)
    @ValidAmount(message = "total")
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "payment_method")
    @ValidNotNull(message = "La forma de pago")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    @Setter(AccessLevel.NONE)
    @Builder.Default
    @ToString.Exclude
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @PreUpdate
    protected void onUpdate() {
        this.date = LocalDate.now();
    }

    public Order(PaymentMethod paymentMethod, Set<OrderDetail> orderDetails) {

        this.paymentMethod = paymentMethod;
        this.orderDetails = orderDetails;

        calculateTotal();
    }

    @Override
    public void calculateTotal() {

        this.total =
                orderDetails.stream()
                        .map(OrderDetail::getSubtotal)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addOrderDetail(int qty, Product product) {

        findOrderDetailByProduct(product)
                .ifPresentOrElse(
                        existing -> existing.setQty(existing.getQuantity() + qty),
                        () -> this.orderDetails.add(OrderDetail.of(qty, product)));

        calculateTotal();
    }

    public void updateProductQty(Product product, int newQty) {

        if (newQty <= 0) {
            deleteOrderDetailByProduct(product);
        } else {
            findOrderDetailByProduct(product).ifPresent(existing -> existing.setQty(newQty));
        }

        calculateTotal();
    }

    public Optional<OrderDetail> findOrderDetailByProduct(Product product) {

        return orderDetails.stream()
                .filter(orderDetail -> orderDetail.getProduct().equals(product))
                .findFirst();
    }

    public void deleteOrderDetailByProduct(Product product) {

        findOrderDetailByProduct(product)
                .ifPresent(
                        orderDetail -> {
                            this.orderDetails.remove(orderDetail);

                            calculateTotal();
                        });
    }
}
