package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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
import java.util.HashSet;
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
    private final StopWordService stopWordService;

    @Transactional
    public BookWithKeywordsDto save(BookWithKeywordsDto bookDto) {
        Book newBook = new Book();
        updateBookByBookDto(bookDto, newBook);

        Book book = bookRepository.save(newBook);

        return bookMapper.mapToDtoWithKeywords(book);
    }

    @Transactional
    public Book updateBook(BookWithKeywordsDto bookDto) {
        var bookToUpdate = bookRepository.findById(bookDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Unable to fetch book with given id: " + bookDto.getId()));

        return updateBookByBookDto(bookDto, bookToUpdate);
    }

    private Book updateBookByBookDto(BookWithKeywordsDto bookDto, Book bookToUpdate) {
        Set<Keyword> keywords = prepareKeywords(bookDto.getKeywords(),
                bookDto.getDescription() + " " + bookDto.getTitle());
        Set<Author> authors = bookDto.getAuthorsIds().stream().map(Author::new).collect(Collectors.toSet());

        keywords.forEach(k -> log.trace(k.getName()));

        bookToUpdate.setTitle(bookDto.getTitle());
        bookToUpdate.setAuthors(authors);
        bookToUpdate.setGenre(new Genre(bookDto.getGenreId()));
        bookToUpdate.setYearOfFirstRelease(bookDto.getYearOfFirstRelease());
        bookToUpdate.setDescription(bookDto.getDescription());
        bookToUpdate.setKeywords(keywords);

        return bookToUpdate;
    }

    private Set<Keyword> prepareKeywords(String keywords, String description) {
        Set<String> keywordNames;
        if (!StringUtils.hasLength(keywords) && description != null) {
            keywordNames = getKeywordsFromDesc(description);
        } else {
            keywordNames = Arrays.stream(keywords.split("\\s*,\\s*"))
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        }

        Set<Keyword> existingKeywords = keywordRepository.findAllByNameIn(keywordNames);
        Set<String> existingKeywordNames = existingKeywords
                .stream()
                .map(Keyword::getName)
                .collect(Collectors.toSet());
        Set<String> parsedKeywordNames = new HashSet<>(keywordNames);
        parsedKeywordNames.removeAll(existingKeywordNames);

        Set<Keyword> parsedKeywords = parsedKeywordNames
                .stream()
                .map(Keyword::new)
                .collect(Collectors.toSet());
        parsedKeywords.addAll(existingKeywords);

        return parsedKeywords;
    }

    private Set<String> getKeywordsFromDesc(String description) {
        Set<String> keywordStopWords = stopWordService.getAllStopWords();

        return Arrays.stream(description.toLowerCase()
                .trim()
                .replaceAll(" +", " ")
                .chars()
                .mapToObj(c -> (char) c)
                .filter(c -> Character.isAlphabetic(c) || (c == ' '))
                .map(String::valueOf)
                .collect(Collectors.joining())
                .split(" "))
                .filter(w -> !keywordStopWords.contains(w))
                .collect(Collectors.toSet());
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
                .map(this::addNumberOfAvailableCopiesToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookDto getBookById(long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unable to fetch book with given id: " + id));

        return bookMapper.mapToDto(book);
    }

    public BookDto addNumberOfAvailableCopiesToDto(BookDto bookDto) {
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
