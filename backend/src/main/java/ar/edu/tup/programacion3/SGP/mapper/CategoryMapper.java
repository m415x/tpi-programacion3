package ar.edu.tup.programacion3.SGP.mapper;

import ar.edu.tup.programacion3.SGP.dto.CategoryResponseDTO;
import ar.edu.tup.programacion3.SGP.dto.CategoryRequestDTO;
import ar.edu.tup.programacion3.SGP.model.Category;
import org.mapstruct.*;

/**
 * Mapeador de la entidad {@link Category} y su DTO {@link CategoryResponseDTO}. También incluye mapeo
 * para la clase {@link CategoryRequestDTO}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
		uses = {ProductMapper.class, StringValidationSupport.class})
public interface CategoryMapper {

	// Para las salidas (GET, respuestas de POST/PUT/PATCH)
    CategoryResponseDTO toDto(Category category);

	// Para las entradas de creación (POST)
    Category toEntity(CategoryRequestDTO dto);

	// Para actualización total (PUT) y parcial (PATCH)
	// Ignora las propiedades nulas para evitar sobrescribir datos existentes con valores null
	@BeanMapping(
			nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
			nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateCategoryFromEdit(CategoryRequestDTO dto, @MappingTarget Category category);
}
