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

	// Mapeador para actualización TOTAL (Pisa campos si se envían vacíos/null intencionalmente)
	@BeanMapping(
			nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
			nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
	void updateCategoryFromEdit(CategoryRequestDTO dto, @MappingTarget Category category);

	// Mapeador para actualización PARCIAL (si es null, se ignora y mantiene el original)
	@BeanMapping(
			nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
			nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
	void partialUpdateCategoryFromEdit(CategoryRequestDTO dto, @MappingTarget Category category);
}
