package pl.rjsk.librarymanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.entity.Book;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = AuthorMapper.class)
public interface BookMapper {

    @Mappings({
            @Mapping(target = "genreId", source = "genre.id"),
            @Mapping(target = "numberOfAvailableCopies", ignore = true)
    })
    BookDto map(Book book);

    List<BookDto> mapAsList(Collection<Book> books);
}
