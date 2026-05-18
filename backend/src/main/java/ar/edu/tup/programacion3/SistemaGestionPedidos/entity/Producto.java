package ar.edu.tup.programacion3.SistemaGestionPedidos.entity;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.*;
import jakarta.persistence.*;
import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "products")
@SQLRestriction("deleted = false")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Producto extends Base {

    @Column(name = "product_name", nullable = false, length = 50)
    @ValidName
    private String nombre;

    @Column(name = "price", nullable = false)
    @ValidAmount(message = "precio")
    private BigDecimal precio;

    @Column(name = "description", length = 200)
    @ValidLongText
    private String descripcion;

    @Column(name = "stock", nullable = false)
    @ValidQty(message = "El stock")
    private Integer stock;

    @Column(name = "image", length = 200)
    @ValidLongText(message = "La imagen")
    private String imagen;

    @Column(name = "available", nullable = false)
    @ValidNotNull
    @Builder.Default
    private Boolean disponible = true;
}
