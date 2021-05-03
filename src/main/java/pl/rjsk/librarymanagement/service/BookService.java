package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rjsk.librarymanagement.mapper.BookMapper;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.dto.BookWithCopiesDto;
import pl.rjsk.librarymanagement.model.dto.BookWithKeywordsDto;
import pl.rjsk.librarymanagement.model.entity.Author;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.BookCopy;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.model.entity.Keyword;
import pl.rjsk.librarymanagement.repository.BookCopyRepository;
import pl.rjsk.librarymanagement.repository.BookHistoryRepository;
import pl.rjsk.librarymanagement.repository.BookRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookHistoryRepository bookHistoryRepository;
    private final BookMapper bookMapper;

    @Transactional
    public void updateBook(BookWithKeywordsDto bookDto) {
        log.info(bookDto.toString());
        var fetchedBook = bookRepository.findById(bookDto.getId());
        if (fetchedBook.isEmpty()) {
            throw new IllegalArgumentException("Unable to fetch book with given id: " + bookDto.getId());
        }

        Book bookToUpdate = fetchedBook.get();

        bookToUpdate.setTitle(bookDto.getTitle());
        bookToUpdate.setAuthors(bookDto.getAuthorsIds().stream().map(Author::new).collect(Collectors.toSet()));
        bookToUpdate.setGenre(new Genre(bookDto.getGenreId()));
        bookToUpdate.setYearOfFirstRelease(bookDto.getYearOfFirstRelease());
        bookToUpdate.setDescription(bookDto.getDescription());
        bookToUpdate.setKeywords(
                Arrays.stream(bookDto.getKeywords().split(" ")).map(Keyword::new).collect(Collectors.toSet()));
    }

    @Transactional
    public BookWithKeywordsDto getBookWithKeywordsById(long id) {
        var book = bookRepository.findById(id);
        if (book.isEmpty()) {
            throw new IllegalArgumentException("Unable to fetch book with given id: " + id);
        }
        BookWithKeywordsDto bookDto = bookMapper.mapToDtoWithKeywords(book.get());

        String keywords = book.get().getKeywords().stream().map(Keyword::getName).collect(Collectors.joining(", "));
        Set<Long> authorIds = book.get().getAuthors().stream().map(Author::getId).collect(Collectors.toSet());

        bookDto.setKeywords(keywords);
        bookDto.setAuthorsIds(authorIds);

        return bookDto;
    }


    @Transactional
    public List<BookDto> getAllBooksToDisplay() {
        return bookMapper.mapIterableToDtoList(bookRepository.findAll())
                .stream()
                .map(this::addNumberOfAvailableCopies)
                .collect(Collectors.toList());
    }

    private BookDto addNumberOfAvailableCopies(BookDto bookDto) {
        List<Long> bookCopyIds =
                bookCopyRepository.findAllByBookId(bookDto.getId())
                        .stream()
                        .map(BookCopy::getId)
                        .collect(Collectors.toList());
        List<Long> notAvailableBookCopyIds = bookHistoryRepository.findAllNotAvailable(bookCopyIds);
        bookCopyIds.removeAll(notAvailableBookCopyIds);

        bookDto.setNumberOfAvailableCopies(bookCopyIds.size());

        return bookDto;
    }

    @Transactional
    public Page<BookWithCopiesDto> getAllBooksWithInstances(Pageable paging) {
        Page<Book> bookPage = bookRepository.findAll(paging);

        List<BookWithCopiesDto> bookList = bookMapper.mapIterableToDtoWithCopiesList(bookPage)
                .stream()
                .map(this::addBookCopies)
                .collect(Collectors.toList());

        return new PageImpl<>(bookList, bookPage.getPageable(), bookPage.getTotalElements());
    }

    private BookWithCopiesDto addBookCopies(BookWithCopiesDto bookWithCopiesDto) {
        Set<Long> bookCopyIds =
                bookCopyRepository.findAllByBookId(bookWithCopiesDto.getId())
                        .stream()
                        .map(BookCopy::getId)
                        .collect(Collectors.toSet());

        bookWithCopiesDto.setBookCopyIds(bookCopyIds);

        return bookWithCopiesDto;
    }
}
