package ar.edu.tup.programacion3.SGP.model;

import ar.edu.tup.programacion3.SGP.validator.ValidNotNull;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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
	@Column(name = "id", updatable = false, nullable = false)
	@GeneratedValue(strategy = GenerationType.UUID)
	protected UUID id;

	@Column(name = "deleted", nullable = false)
	@ValidNotNull
	@Builder.Default
	protected Boolean deleted = false;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	protected LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@Column(name = "version", nullable = false)
	@Version
	private Long version;

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
