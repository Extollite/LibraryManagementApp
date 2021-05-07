package pl.rjsk.librarymanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import pl.rjsk.librarymanagement.model.dto.AuthorDto;
import pl.rjsk.librarymanagement.model.entity.Author;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorDto map(Author author);

    List<AuthorDto> mapAsList(Collection<Author> authors);

    @Named("getAuthorsIds")
    default Set<Long> getAuthorsIds(Set<Author> authors) {
        return authors
                .stream()
                .map(Author::getId)
                .collect(Collectors.toSet());
    }
}
