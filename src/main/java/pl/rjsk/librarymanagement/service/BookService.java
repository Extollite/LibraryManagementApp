package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import pl.rjsk.librarymanagement.exception.ResourceNotFoundException;
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

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    private final Set<String> keywordStopWords = new HashSet<>();

    @Value("${keywords.stopwords.filename}")
    private String keywordsStopWordsFilename;

    @PostConstruct
    private void loadKeywordStopWordsFromFile() {
        try {
            File file = ResourceUtils.getFile(keywordsStopWordsFilename);
            List<String> lines = Files.readAllLines(file.toPath());
            keywordStopWords.addAll(lines);
        } catch (IOException ex) {
            log.error("Exception: ", ex);
        }
    }

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
                .orElseThrow(() -> new ResourceNotFoundException("Unable to fetch book with given id: "
                        + bookDto.getId()));

        updateBookByBookDto(bookDto, bookToUpdate);
    }

    private void updateBookByBookDto(BookWithKeywordsDto bookDto, Book bookToUpdate) {
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
        keywordNames.removeAll(existingKeywordNames);

        Set<Keyword> parsedKeywords = keywordNames
                .stream()
                .map(Keyword::new)
                .collect(Collectors.toSet());
        parsedKeywords.addAll(existingKeywords);

        return parsedKeywords;
    }

    private Set<String> getKeywordsFromDesc(String description) {
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
                .orElseThrow(() -> new ResourceNotFoundException("Unable to fetch book with given id: " + id));

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
                .orElseThrow(() -> new ResourceNotFoundException("Unable to fetch book with given id: " + id));

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
