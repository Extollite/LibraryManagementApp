package pl.rjsk.librarymanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.rjsk.librarymanagement.model.dto.BookDisplayDto;
import pl.rjsk.librarymanagement.model.entity.Book;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = AuthorMapper.class)
public interface BookMapper {

    @Mappings({
            @Mapping(target = "genreId", source = "genre.id"),
            @Mapping(target = "bookInstanceIds", ignore = true)
    })
    BookDisplayDto map(Book book);

    List<BookDisplayDto> mapAsList(Collection<Book> books);
}
