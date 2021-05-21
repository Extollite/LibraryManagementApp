package pl.rjsk.librarymanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.rjsk.librarymanagement.model.dto.BookRatingDto;
import pl.rjsk.librarymanagement.model.entity.BookRating;

@Mapper(componentModel = "spring")
public interface BookRatingMapper {
    @Mappings({
            @Mapping(target = "bookId", source = "book.id"),
            @Mapping(target = "userId", source = "user.id")
    })
    BookRatingDto mapToDto(BookRating bookRating);
}
