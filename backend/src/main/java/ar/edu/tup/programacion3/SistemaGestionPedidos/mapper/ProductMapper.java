package ar.edu.tup.programacion3.SistemaGestionPedidos.mapper;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Product;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "categoryId")
    ProductDto toDto(Product product, Long categoryId);

    Product toEntity(ProductCreate productCreate);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromEdit(ProductEdit productEdit, @MappingTarget Product product);
}
