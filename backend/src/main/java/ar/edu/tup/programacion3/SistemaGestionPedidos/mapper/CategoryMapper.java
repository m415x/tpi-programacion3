package ar.edu.tup.programacion3.SistemaGestionPedidos.mapper;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Category;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = ProductMapper.class)
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toEntity(CategoryCreate categoryCreate);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategoryFromEdit(CategoryEdit categoryEdit, @MappingTarget Category category);
}
