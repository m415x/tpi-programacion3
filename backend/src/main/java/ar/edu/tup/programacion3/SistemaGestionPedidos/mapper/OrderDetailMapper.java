package ar.edu.tup.programacion3.SistemaGestionPedidos.mapper;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderDetailResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapeador de la entidad {@link OrderDetail} y su DTO {@link OrderDetailResponseDTO}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ProductMapper.class, StringValidationSupport.class})
public interface OrderDetailMapper {

	// Para las salidas (GET, respuestas de POST/PUT/PATCH)
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "product.categoryId", source = "categoryId")
    OrderDetailResponseDTO toDto(OrderDetail detail, Long orderId, Long categoryId);
}
