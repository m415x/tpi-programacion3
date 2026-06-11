package ar.edu.tup.programacion3.SistemaGestionPedidos.mapper;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Order;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "userId", ignore = true)
    OrderResponseDTO toDto(Order order);

    Order toEntity(OrderRequestDTO orderRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOrderFromEdit(OrderRequestDTO orderRequestDTO, @MappingTarget Order order);
}
