package ar.edu.tup.programacion3.SGP.mapper;

import ar.edu.tup.programacion3.SGP.dto.OrderDetailResponseDTO;
import ar.edu.tup.programacion3.SGP.model.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

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
    OrderDetailResponseDTO toDto(OrderDetail detail, UUID orderId, UUID categoryId);
}
