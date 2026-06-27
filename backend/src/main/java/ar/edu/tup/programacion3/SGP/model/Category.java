package ar.edu.tup.programacion3.SGP.model;

import ar.edu.tup.programacion3.SGP.validator.ValidLongText;
import ar.edu.tup.programacion3.SGP.validator.ValidName;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "categories")
@SQLRestriction("deleted = false")
@DynamicUpdate
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Category extends Base {

    @Column(name = "name", nullable = false, length = 50)
    @ValidName
    private String name;

    @Column(name = "description", length = 200)
    @ValidLongText
    private String description;

	@Column(name = "image", length = 200)
	@ValidLongText(message = "La image")
	private String image;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @Setter(AccessLevel.NONE)
    @Builder.Default
    @ToString.Exclude
    private Set<Product> products = new HashSet<>();

    public void addProduct(Product product) {
        this.products.add(product);
    }
}
