package ar.edu.tup.programacion3.SistemaGestionPedidos.mapper;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.orderDetail.OrderDetailDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ProductMapper.class})
public interface OrderDetailMapper {

    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "product.categoryId", source = "categoryId")
    OrderDetailDto toDto(OrderDetail orderDetail, Long orderId, Long categoryId);
}
