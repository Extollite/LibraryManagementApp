package pl.rjsk.librarymanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.dto.BookWithCopiesDto;
import pl.rjsk.librarymanagement.model.dto.BookWithKeywordsDto;
import pl.rjsk.librarymanagement.model.dto.BookWithRatingDto;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.Keyword;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Mappings({
            @Mapping(target = "genreId", source = "genre.id"),
            @Mapping(target = "keywords", qualifiedByName = "joinKeywords"),
            @Mapping(target = "authorsIds", source = "authors", qualifiedByName = "getAuthorsIds")
    })
    BookWithKeywordsDto mapToDtoWithKeywords(Book book);

    @Mappings({
            @Mapping(target = "genreId", source = "genre.id"),
            @Mapping(target = "rating", ignore = true),
            @Mapping(target = "authorsIds", source = "authors", qualifiedByName = "getAuthorsIds")
    })
    BookWithRatingDto mapToBookWithRating(Book book);

    @Named("joinKeywords")
    default String joinKeywords(Set<Keyword> keywords) {
        return keywords
                .stream()
                .map(Keyword::getName)
                .collect(Collectors.joining(", "));
    }
}
