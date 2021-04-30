package pl.rjsk.librarymanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.dto.BookWithCopiesDto;
import pl.rjsk.librarymanagement.model.entity.Book;

import java.util.List;

@Mapper(componentModel = "spring", uses = AuthorMapper.class)
public interface BookMapper {

    @Mappings({
            @Mapping(target = "genreId", source = "genre.id"),
            @Mapping(target = "numberOfAvailableCopies", ignore = true)
    })
    BookDto mapToDto(Book book);

    List<BookDto> mapIterableToDtoList(Iterable<Book> books);

    @Mappings({
            @Mapping(target = "genreId", source = "genre.id"),
            @Mapping(target = "bookCopyIds", ignore = true)
    })
    BookWithCopiesDto mapToDtoWithCopies(Book book);

    List<BookWithCopiesDto> mapIterableToDtoWithCopiesList(Iterable<Book> books);
}
