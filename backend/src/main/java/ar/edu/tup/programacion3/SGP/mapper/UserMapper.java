package ar.edu.tup.programacion3.SGP.mapper;

import ar.edu.tup.programacion3.SGP.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.UserResponseDTO;
import ar.edu.tup.programacion3.SGP.model.User;
import org.mapstruct.*;

/**
 * Mapeador de la entidad {@link User} y su DTO {@link UserResponseDTO}. También incluye mapeo para
 * la clase {@link UserRequestDTO}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    // Para las salidas (GET, respuestas de POST/PUT/PATCH)
    UserResponseDTO toDto(User user);

    // Para las entradas de creación (POST)
    User toEntity(UserRequestDTO dto);

    // Para actualización total (PUT) y parcial (PATCH)
    // Ignora las propiedades nulas para evitar sobrescribir datos existentes con valores null
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateUserFromEdit(UserRequestDTO dto, @MappingTarget User user);
}
