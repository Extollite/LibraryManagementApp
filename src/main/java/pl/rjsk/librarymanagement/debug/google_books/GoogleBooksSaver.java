package pl.rjsk.librarymanagement.debug.google_books;

import com.google.api.services.books.v1.model.Volume;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.rjsk.librarymanagement.mapper.AuthorMapper;
import pl.rjsk.librarymanagement.model.dto.AuthorDto;
import pl.rjsk.librarymanagement.model.dto.BookCopyDueDateDto;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.dto.BookWithKeywordsDto;
import pl.rjsk.librarymanagement.model.entity.Author;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.service.AuthorService;
import pl.rjsk.librarymanagement.service.BookCopyService;
import pl.rjsk.librarymanagement.service.BookService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleBooksSaver {

    private final BookService bookService;
    private final BookCopyService bookCopyService;
    private final AuthorService authorService;
    private final AuthorMapper authorMapper;

    public List<Volume> saveBooksFromQuery(Genre genre, String title) {
        List<Volume> volumes = GoogleBooksFetcher.fetchBooksFromAPI(title, genre);

        volumes.forEach(v -> processVolume(v, genre));

        return volumes;
    }

    private void processVolume(Volume volume, Genre genre) {
        BookDto bookDto = GoogleBooksMapper.mapVolumeToBook(volume, genre.getId());

        Set<Long> authorsIds = saveNewAuthors(bookDto.getAuthors());

        BookWithKeywordsDto bookWithKeywordsDto = GoogleBooksMapper.mapToBookWithKeywordsDto(bookDto, authorsIds);
        BookWithKeywordsDto savedBook = saveNewBook(bookWithKeywordsDto);

        saveNewCopy(volume, savedBook);
    }

    private Set<Long> saveNewAuthors(List<AuthorDto> authors) {
        List<AuthorDto> currentAuthors = authorService.getAllAuthors().stream()
                .map(authorMapper::mapToDto)
                .collect(Collectors.toList());

        List<AuthorDto> newAuthors = authors.stream()
                .filter(a -> !currentAuthors.contains(a))
                .collect(Collectors.toList());

        Set<Long> authorsIds = authorService.saveAll(newAuthors).stream()
                .map(AuthorDto::getId)
                .collect(Collectors.toSet());
        
        authors.forEach(author -> {
            currentAuthors.stream()
                    .filter(curr -> curr.equals(author))
                    .map(AuthorDto::getId)
                    .forEach(authorsIds::add);
        });
        
        return authorsIds;
    }

    private BookWithKeywordsDto saveNewBook(BookWithKeywordsDto bookWithKeywordsDto) {
        return bookService.save(bookWithKeywordsDto);
    }

    private void saveNewCopy(Volume volume, BookWithKeywordsDto bookWithKeywordsDto) {
        BookCopyDueDateDto bookCopyDto = GoogleBooksMapper.mapToBookCopyDueDateDto(volume, bookWithKeywordsDto);
        bookCopyService.saveBookCopy(bookCopyDto);
    }
}
