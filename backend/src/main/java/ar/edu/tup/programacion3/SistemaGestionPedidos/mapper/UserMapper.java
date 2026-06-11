package ar.edu.tup.programacion3.SistemaGestionPedidos.mapper;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.User;
import org.mapstruct.*;

/**
 * Mapeador de la entidad {@link User} y su DTO {@link UserResponseDTO}. También incluye mapeos para las
 * clases {@link UserRequestDTO} y {@link UserRequestDTO}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    // Mapeo entre User y UserDto
    UserResponseDTO toDto(User user);

    // Mapeo entre UserDto y User
    User toEntity(UserResponseDTO userResponseDTO);

    // Mapeo entre UserRequestDTO y User
    User toEntity(UserRequestDTO userRequestDTO);

    // Mapeo entre UserRequestDTO y User
    // Ignora las propiedades nulas para evitar sobrescribir datos existentes con valores null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromEdit(UserRequestDTO userRequestDTO, @MappingTarget User user);
}
