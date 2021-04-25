package pl.rjsk.librarymanagement.mapper;

import org.mapstruct.Mapper;
import pl.rjsk.librarymanagement.model.dto.AuthorDto;
import pl.rjsk.librarymanagement.model.entity.Author;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorDto map(Author author);

    List<AuthorDto> mapAsList(Collection<Author> authors);
}
