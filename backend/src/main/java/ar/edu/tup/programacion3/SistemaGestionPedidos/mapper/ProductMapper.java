package ar.edu.tup.programacion3.SistemaGestionPedidos.mapper;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Product;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "categoryId")
    ProductResponseDTO toDto(Product product, Long categoryId);

    Product toEntity(ProductRequestDTO productRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromEdit(ProductRequestDTO productRequestDTO, @MappingTarget Product product);
}
