package ar.edu.tup.programacion3.SGP.mapper;

import ar.edu.tup.programacion3.SGP.dto.ProductResponseDTO;
import ar.edu.tup.programacion3.SGP.dto.ProductRequestDTO;
import ar.edu.tup.programacion3.SGP.model.Category;
import ar.edu.tup.programacion3.SGP.model.Product;
import org.mapstruct.*;

import java.util.UUID;

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
	@Mapping(target = "id", source = "product.id")
	@Mapping(target = "name", source = "product.name")
	@Mapping(target = "price", source = "product.price")
	@Mapping(target = "description", source = "product.description")
	@Mapping(target = "stock", source = "product.stock")
	@Mapping(target = "image", source = "product.image")
	@Mapping(target = "available", source = "product.available")
	@Mapping(target = "category.id", source = "category.id")
	@Mapping(target = "category.name", source = "category.name")
	@Mapping(target = "category.description", source = "category.description")
    ProductResponseDTO toDto(Product product, Category category);

	// Para las entradas de creación (POST)
    Product toEntity(ProductRequestDTO dto);

	// Para actualización total (PUT) y parcial (PATCH)
	// Ignora las propiedades nulas para evitar sobrescribir datos existentes con valores null
	@BeanMapping(
			nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
			nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateProductFromEdit(ProductRequestDTO dto, @MappingTarget Product product);
}
