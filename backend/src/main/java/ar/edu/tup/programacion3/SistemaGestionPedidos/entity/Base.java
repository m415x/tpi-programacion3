package ar.edu.tup.programacion3.SistemaGestionPedidos.entity;

import java.time.LocalDateTime;

import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.ValidNotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

@MappedSuperclass
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "deleted", nullable = false)
    @ValidNotNull
    @Builder.Default
    protected Boolean eliminado = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @ValidNotNull(message = "La fecha y hora de creación")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    protected LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Base)) return false;
        Base other = (Base) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
