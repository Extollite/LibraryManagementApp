package pl.rjsk.librarymanagement.debug;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.rjsk.librarymanagement.mapper.AuthorMapper;
import pl.rjsk.librarymanagement.model.dto.AuthorDto;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.dto.BookWithKeywordsDto;
import pl.rjsk.librarymanagement.model.entity.Author;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.service.AuthorService;
import pl.rjsk.librarymanagement.service.BookService;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BooksInfoSaver {
    private final BookService bookService;
    private final AuthorService authorService;
    private final AuthorMapper authorMapper;
    
    
    public List<BookWithKeywordsDto> saveBooksFromQuery(Genre genre, String title) {
        List<BookDto> booksDtos = BooksInfoFetcher.fetchBooksFromAPI(title, genre);
        
        List<AuthorDto> newAuthors = getNewAuthors(booksDtos);
        saveNewAuthors(newAuthors);
        
        List<BookWithKeywordsDto> booksWithKeywordsDtos = booksDtos.stream()
                .map(this::mapToBookWithKeywordsDto)
                .collect(Collectors.toList());
        saveNewBooks(booksWithKeywordsDtos);
        
        return booksWithKeywordsDtos;
    }
    
    private void saveNewAuthors(List<AuthorDto> newAuthors) {
        newAuthors.forEach(authorService::save);
    }
    
    private void saveNewBooks(List<BookWithKeywordsDto> bookWithKeywordsDtos) {
        bookWithKeywordsDtos.forEach(bookService::save);
    }
    
    private List<AuthorDto> getNewAuthors(List<BookDto> bookDtos) {
        List<AuthorDto> currentAuthors = authorService.getAllAuthors().stream()
                .map(authorMapper::mapToDto)
                .collect(Collectors.toList());
        
        List<AuthorDto> uniqueNewAuthors = bookDtos.stream()
                .map(BookDto::getAuthors).flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        
        return uniqueNewAuthors.stream()
                .filter(a -> !currentAuthors.contains(a))
                .collect(Collectors.toList());
    }
    
    private BookWithKeywordsDto mapToBookWithKeywordsDto(BookDto bookDto) {
        BookWithKeywordsDto bookWithKeywordsDto = new BookWithKeywordsDto();
        
        Set<Long> authorsIds = getAuthorIds(bookDto.getAuthors());
        
        bookWithKeywordsDto.setTitle(bookDto.getTitle());
        bookWithKeywordsDto.setGenreId(bookDto.getGenreId());
        bookWithKeywordsDto.setAuthorsIds(authorsIds);
        bookWithKeywordsDto.setYearOfFirstRelease(bookDto.getYearOfFirstRelease());
        bookWithKeywordsDto.setDescription(bookDto.getDescription());
        bookWithKeywordsDto.setKeywords("");
        
        return bookWithKeywordsDto;
    }
    
    private Set<Long> getAuthorIds(List<AuthorDto> authorsDtos) {
        return authorsDtos.stream()
                .map(authorDto -> {
                    Author author =
                            authorService.getByFirstLastName(authorDto.getFirstName(), authorDto.getLastName());
                    return author.getId();
                })
                .collect(Collectors.toSet());
    }
}
