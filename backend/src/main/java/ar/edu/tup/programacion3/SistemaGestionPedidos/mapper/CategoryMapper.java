package ar.edu.tup.programacion3.SistemaGestionPedidos.mapper;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Category;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = ProductMapper.class)
public interface CategoryMapper {

    CategoryResponseDTO toDto(Category category);

    Category toEntity(CategoryRequestDTO categoryRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategoryFromEdit(CategoryRequestDTO categoryRequestDTO, @MappingTarget Category category);
}
