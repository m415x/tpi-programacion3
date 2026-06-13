package ar.edu.tup.programacion3.SistemaGestionPedidos.mapper;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Order;
import org.mapstruct.*;

/**
 * Mapeador de la entidad {@link Order} y su DTO {@link OrderResponseDTO}. También incluye mapeo
 * para la clase {@link OrderRequestDTO}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
		uses = StringValidationSupport.class)
public interface OrderMapper {

	// Para las salidas (GET, respuestas de POST/PUT/PATCH)
    @Mapping(target = "userId", ignore = true)
    OrderResponseDTO toDto(Order order);

	// Para las entradas de creación (POST)
    Order toEntity(OrderRequestDTO dto);

	// Para actualización total (PUT) y parcial (PATCH)
	// Ignora las propiedades nulas para evitar sobrescribir datos existentes con valores null
	@BeanMapping(
			nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
			nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateOrderFromEdit(OrderRequestDTO dto, @MappingTarget Order order);
}
