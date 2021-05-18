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
import pl.rjsk.librarymanagement.repository.KeywordRepository;

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
    private final KeywordRepository keywordRepository;

    @Transactional
    public BookWithKeywordsDto save(BookWithKeywordsDto bookDto) {
        Book newBook = new Book();
        updateBookByBookDto(bookDto, newBook);

        var book = bookRepository.save(newBook);

        return bookMapper.mapToDtoWithKeywords(book);
    }

    //TODO: test logic
    @Transactional
    public void updateBook(BookWithKeywordsDto bookDto) {
        var bookToUpdate = bookRepository.findById(bookDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Unable to fetch book with given id: " + bookDto.getId()));

        updateBookByBookDto(bookDto, bookToUpdate);
    }

    private void updateBookByBookDto(BookWithKeywordsDto bookDto, Book bookToUpdate) {
        Set<Keyword> keywords = prepareKeywords(bookDto.getKeywords());
        Set<Author> authors = bookDto.getAuthorsIds().stream().map(Author::new).collect(Collectors.toSet());

        bookToUpdate.setTitle(bookDto.getTitle());
        bookToUpdate.setAuthors(authors);
        bookToUpdate.setGenre(new Genre(bookDto.getGenreId()));
        bookToUpdate.setYearOfFirstRelease(bookDto.getYearOfFirstRelease());
        bookToUpdate.setDescription(bookDto.getDescription());
        bookToUpdate.setKeywords(keywords);
    }

    private Set<Keyword> prepareKeywords(String keywords) {
        Set<String> keywordNames = Arrays.stream(keywords.split("\\s*,\\s*"))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        Set<Keyword> existingKeywords = keywordRepository.findAllByNameIn(keywordNames);
        Set<String> existingKeywordNames = existingKeywords
                .stream()
                .map(Keyword::getName)
                .collect(Collectors.toSet());
        keywordNames.removeAll(existingKeywordNames);

        Set<Keyword> parsedKeywords = keywordNames
                .stream()
                .map(Keyword::new)
                .collect(Collectors.toSet());
        parsedKeywords.addAll(existingKeywords);

        return parsedKeywords;
    }

    @Transactional
    public BookWithKeywordsDto getBookWithKeywordsById(long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unable to fetch book with given id: " + id));

        return bookMapper.mapToDtoWithKeywords(book);
    }

    @Transactional
    public List<BookDto> getAllBooksToDisplay() {
        return bookMapper.mapIterableToDtoList(bookRepository.findAll())
                .stream()
                .map(this::addNumberOfAvailableCopies)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookDto getBookById(long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unable to fetch book with given id: " + id));

        return bookMapper.mapToDto(book);
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
