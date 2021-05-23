package pl.rjsk.librarymanagement.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ResourceUtils;
import pl.rjsk.librarymanagement.mapper.BookMapper;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.dto.BookWithCopiesDto;
import pl.rjsk.librarymanagement.model.dto.BookWithKeywordsDto;
import pl.rjsk.librarymanagement.model.entity.Author;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.BookCopy;
import pl.rjsk.librarymanagement.model.entity.Keyword;
import pl.rjsk.librarymanagement.repository.BookCopyRepository;
import pl.rjsk.librarymanagement.repository.BookHistoryRepository;
import pl.rjsk.librarymanagement.repository.BookRepository;
import pl.rjsk.librarymanagement.repository.KeywordRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    private static final long BOOK_ID = 1L;
    private static final long BOOK_COPY_ID = 1L;
    private static final int NUM_OF_COPIES = 1;

    private static final Set<String> KEYWORD_STOP_WORDS = new HashSet<>();

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private BookHistoryRepository bookHistoryRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private KeywordRepository keywordRepository;

    @Mock
    private StopWordService stopWordService;

    @InjectMocks
    private BookService bookService;

    @BeforeAll
    static void loadStopWords() {
        try {
            File file = ResourceUtils.getFile("classpath:words_to_filter.txt");
            List<String> lines = Files.readAllLines(file.toPath());
            KEYWORD_STOP_WORDS.addAll(lines);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void save() {
        Set<Long> authorsIds = Set.of(1L, 2L);
        String description = "A fantastic book about the power of friendship.";
        String keywordsString = "friendship, adventure, power, friendship";

        BookWithKeywordsDto bookDto = new BookWithKeywordsDto();
        bookDto.setTitle("");
        bookDto.setAuthorsIds(authorsIds);
        bookDto.setDescription(description);
        bookDto.setKeywords(keywordsString);

        Set<Keyword> bookKeywordsAlreadyPresent = new LinkedHashSet<>();
        bookKeywordsAlreadyPresent.add(new Keyword("adventure"));
        bookKeywordsAlreadyPresent.add(new Keyword("power"));

        Set<String> bookKeywordNames = new LinkedHashSet<>();
        bookKeywordNames.add("friendship");
        bookKeywordNames.add("adventure");
        bookKeywordNames.add("power");

        String bookKeywordsString = "friendship, adventure, power";

        BookWithKeywordsDto newBookDto = new BookWithKeywordsDto();
        newBookDto.setId(BOOK_ID);
        newBookDto.setAuthorsIds(authorsIds);
        newBookDto.setDescription(description);
        newBookDto.setKeywords(bookKeywordsString);

        when(keywordRepository.findAllByNameIn(anyCollection())).thenReturn(bookKeywordsAlreadyPresent);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookMapper.mapToDtoWithKeywords(any(Book.class))).thenReturn(newBookDto);

        BookWithKeywordsDto result = bookService.save(bookDto);

        assertThat(result)
                .isNotNull()
                .extracting("id", "authorsIds", "description", "keywords")
                .containsExactly(BOOK_ID, authorsIds, description, bookKeywordsString);

        verify(keywordRepository).findAllByNameIn(eq(bookKeywordNames));
        verify(bookRepository).save(any(Book.class));
        verify(bookMapper).mapToDtoWithKeywords(any(Book.class));
        verifyNoInteractions(bookHistoryRepository, bookCopyRepository, stopWordService);
    }

    @Test
    void update() {
        Set<Long> authorsIds = Set.of(1L, 2L);

        final String description = "A fantastic book about the power of friendship.";

        BookWithKeywordsDto bookDto = new BookWithKeywordsDto();
        bookDto.setId(BOOK_ID);
        bookDto.setTitle("");
        bookDto.setAuthorsIds(authorsIds);
        bookDto.setDescription(description);
        bookDto.setKeywords(null);

        Set<String> bookKeywordNames = new LinkedHashSet<>();
        bookKeywordNames.add("friendship");
        bookKeywordNames.add("book");
        bookKeywordNames.add("fantastic");
        bookKeywordNames.add("power");

        Set<Keyword> bookKeywords = new HashSet<>();
        bookKeywords.add(new Keyword("friendship"));
        bookKeywords.add(new Keyword("book"));
        bookKeywords.add(new Keyword("fantastic"));
        bookKeywords.add(new Keyword("power"));

        var book = new Book();
        book.setId(BOOK_ID);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(keywordRepository.findAllByNameIn(anyCollection())).thenReturn(Collections.emptySet());
        when(stopWordService.getAllStopWords()).thenReturn(KEYWORD_STOP_WORDS);

        Book result = bookService.updateBook(bookDto);

        assertThat(result)
                .isNotNull()
                .matches(res -> Objects.equals(res.getId(), BOOK_ID))
                .matches(res -> Objects.equals(res.getDescription(), description))
                .matches(res -> res.getAuthors().containsAll(Set.of(new Author(1L), new Author(2L))))
                .matches(res -> res.getKeywords().containsAll(bookKeywords));

        verify(bookRepository).findById(eq(BOOK_ID));
        verify(keywordRepository).findAllByNameIn(eq(bookKeywordNames));
        verify(stopWordService).getAllStopWords();
        verifyNoInteractions(bookHistoryRepository, bookCopyRepository, bookMapper);
    }

    @Test
    void update_exceptionThrown() {
        BookWithKeywordsDto bookDto = new BookWithKeywordsDto();
        bookDto.setId(BOOK_ID);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(bookDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unable to fetch book with given id: " + bookDto.getId());

        verify(bookRepository).findById(eq(BOOK_ID));
        verifyNoInteractions(keywordRepository, stopWordService, bookHistoryRepository, bookCopyRepository, bookMapper);
    }

    @Test
    void getBookWithKeywordsById() {
        Book book = new Book();
        book.setId(BOOK_ID);

        BookWithKeywordsDto bookDto = new BookWithKeywordsDto();
        bookDto.setId(BOOK_ID);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookMapper.mapToDtoWithKeywords(any(Book.class))).thenReturn(bookDto);

        var result = bookService.getBookWithKeywordsById(BOOK_ID);

        assertThat(result.getId()).isEqualTo(BOOK_ID);

        verify(bookRepository).findById(eq(BOOK_ID));
        verify(bookMapper).mapToDtoWithKeywords(eq(book));
        verifyNoInteractions(bookHistoryRepository, bookCopyRepository, keywordRepository, stopWordService);
    }

    @Test
    void getBookWithKeywordsById_InvalidId() {
        String expectedMessage = "Unable to fetch book with given id: " + BOOK_ID;

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookWithKeywordsById(BOOK_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);

        verify(bookRepository).findById(eq(BOOK_ID));
        verifyNoInteractions(bookHistoryRepository, bookCopyRepository, bookMapper, keywordRepository, stopWordService);
    }

    @Test
    void getAllBooksToDisplay() {
        List<Book> books = Collections.emptyList();

        var bookDto = new BookDto();
        bookDto.setId(BOOK_ID);

        var bookCopy = new BookCopy();
        bookCopy.setId(BOOK_COPY_ID);

        List<Long> bookCopyIds = List.of(BOOK_ID);

        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.mapIterableToDtoList(anyCollection())).thenReturn(List.of(bookDto));
        when(bookCopyRepository.findAllByBookId(anyLong())).thenReturn(List.of(bookCopy));
        when(bookHistoryRepository.findAllNotAvailable(anyCollection())).thenReturn(Collections.emptyList());

        List<BookDto> result = bookService.getAllBooksToDisplay();

        assertThat(result)
                .hasSize(1)
                .extracting("id", "numberOfAvailableCopies")
                .containsExactly(tuple(BOOK_ID, NUM_OF_COPIES));

        verify(bookRepository).findAll();
        verify(bookMapper).mapIterableToDtoList(eq(books));
        verify(bookCopyRepository).findAllByBookId(eq(BOOK_ID));
        verify(bookHistoryRepository).findAllNotAvailable(eq(bookCopyIds));
        verifyNoInteractions(keywordRepository, stopWordService);
    }

    @Test
    void getAllBooksWithInstances() {
        long totalElements = 2;
        Pageable paging = PageRequest.of(1, 1);
        Page<Book> bookPage = new PageImpl<>(Collections.emptyList(), paging, totalElements);

        var bookWithCopiesDto = new BookWithCopiesDto();
        bookWithCopiesDto.setId(BOOK_ID);

        var bookCopy = new BookCopy();
        bookCopy.setId(BOOK_COPY_ID);

        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.mapIterableToDtoWithCopiesList(any())).thenReturn(List.of(bookWithCopiesDto));
        when(bookCopyRepository.findAllByBookId(anyLong())).thenReturn(List.of(bookCopy));

        Page<BookWithCopiesDto> result = bookService.getAllBooksWithInstances(paging);

        assertThat(result)
                .isNotNull();
        assertThat(result.getPageable())
                .isNotNull()
                .isEqualTo(paging);
        assertThat(result.getTotalElements())
                .isEqualTo(totalElements);
        assertThat(result.getContent())
                .hasSize(1)
                .extracting("id", "bookCopyIds")
                .containsExactly(tuple(BOOK_ID, Set.of(BOOK_COPY_ID)));

        verify(bookRepository).findAll(eq(paging));
        verify(bookMapper).mapIterableToDtoWithCopiesList(eq(bookPage));
        verify(bookCopyRepository).findAllByBookId(eq(BOOK_ID));
        verifyNoInteractions(bookHistoryRepository, keywordRepository, stopWordService);
    }
}
