package pl.rjsk.librarymanagement.mapper;

import org.mapstruct.Mapper;
import pl.rjsk.librarymanagement.model.dto.UserDto;
import pl.rjsk.librarymanagement.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto mapToDto(User User);
}
