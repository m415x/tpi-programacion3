package ar.edu.tup.programacion3.SGP.model;

import ar.edu.tup.programacion3.SGP.validator.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(
        name = "products",
        indexes = {
            // Índices individuales para filtros simples
            @Index(name = "idx_product_name", columnList = "name"),
            @Index(name = "idx_product_price", columnList = "price"),
            @Index(name = "idx_product_available", columnList = "available"),
            // Índices compuestos para filtros complejos
            @Index(name = "idx_product_category_name", columnList = "category_id, name"),
            @Index(name = "idx_product_category_price", columnList = "category_id, price"),
        })
@SQLRestriction("deleted = false")
@DynamicUpdate
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends Base {

    @Column(name = "name", nullable = false, length = 50)
    @ValidName
    private String name;

    @Column(name = "price", nullable = false)
    @ValidAmount(message = "price")
    private BigDecimal price;

    @Column(name = "description", length = 200)
    @ValidLongText
    private String description;

    @Column(name = "stock", nullable = false)
    @ValidQty(message = "El stock")
    private Integer stock;

    @Column(name = "image", length = 200)
    @ValidLongText(message = "La image")
    private String image;

    @Column(name = "available", nullable = false)
    @ValidNotNull
    @Builder.Default
    private Boolean available = true;
}
