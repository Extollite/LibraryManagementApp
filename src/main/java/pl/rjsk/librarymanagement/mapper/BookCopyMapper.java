package pl.rjsk.librarymanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.rjsk.librarymanagement.model.dto.BookCopyDueDateDto;
import pl.rjsk.librarymanagement.model.entity.BookCopy;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BookCopyMapper {

    @Mappings({
            @Mapping(target = "bookId", source = "book.id"),
            @Mapping(target = "available", ignore = true),
            @Mapping(target = "dueDate", ignore = true)
    })
    BookCopyDueDateDto map(BookCopy bookCopy);

    List<BookCopyDueDateDto> mapAsList(Collection<BookCopy> bookCopies);
}
