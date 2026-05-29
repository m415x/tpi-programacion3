package ar.edu.tup.programacion3.SistemaGestionPedidos.mapper;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.user.UserCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.user.UserDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.user.UserEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.User;
import org.mapstruct.*;

/**
 * Mapeador de la entidad {@link User} y su DTO {@link UserDto}. También incluye mapeos para las
 * clases {@link UserCreate} y {@link UserEdit}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    // Mapeo entre User y UserDto
    UserDto toDto(User user);

    // Mapeo entre UserDto y User
    User toEntity(UserDto userDto);

    // Mapeo entre UserCreate y User
    User toEntity(UserCreate userCreate);

    // Mapeo entre UserEdit y User
    // Ignora las propiedades nulas para evitar sobrescribir datos existentes con valores null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromEdit(UserEdit userEdit, @MappingTarget User user);
}
