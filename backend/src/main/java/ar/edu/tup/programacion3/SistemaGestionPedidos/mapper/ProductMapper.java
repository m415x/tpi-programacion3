package ar.edu.tup.programacion3.SistemaGestionPedidos.mapper;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Product;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.User;
import org.mapstruct.*;

/**
 * Mapeador de la entidad {@link Product} y su DTO {@link ProductResponseDTO}. También incluye mapeo para la
 * clase {@link ProductRequestDTO}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
		uses = StringValidationSupport.class)
public interface ProductMapper {

	// Para las salidas (GET, respuestas de POST/PUT/PATCH)
    @Mapping(target = "categoryId", source = "categoryId")
    ProductResponseDTO toDto(Product product, Long categoryId);

	// Para las entradas de creación (POST)
    Product toEntity(ProductRequestDTO dto);

	// Para actualización total (PUT) y parcial (PATCH)
	// Ignora las propiedades nulas para evitar sobrescribir datos existentes con valores null
	@BeanMapping(
			nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
			nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateProductFromEdit(ProductRequestDTO dto, @MappingTarget Product product);
}
