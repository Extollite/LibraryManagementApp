package pl.rjsk.librarymanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
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

    @InjectMocks
    private BookService bookService;

    @Test
    void save() {
        Set<Long> authorsIds = Set.of(1L, 2L);
        String description = "A fantastic book about the power of friendship.";
        String keywordsString = "friendship, adventure, power, friendship";
        
        BookWithKeywordsDto bookDto = new BookWithKeywordsDto();
        bookDto.setAuthorsIds(authorsIds);
        bookDto.setDescription(description);
        bookDto.setKeywords(keywordsString);
        
        Set<Keyword> bookKeywordsAlreadyPresent = new LinkedHashSet<>();
        bookKeywordsAlreadyPresent.add(new Keyword("adventure"));
        bookKeywordsAlreadyPresent.add(new Keyword("power"));
      
        Set<Keyword> bookKeywords = new LinkedHashSet<>();
        bookKeywords.add(new Keyword("friendship"));
        bookKeywords.add(new Keyword("adventure"));
        bookKeywords.add(new Keyword("power"));
        
        Set<String> bookKeywordNames = new LinkedHashSet<>();
        bookKeywordNames.add("friendship");
        bookKeywordNames.add("adventure");
        bookKeywordNames.add("power");

        String bookKeywordsString = "friendship, adventure, power";
        
        Author authorA = new Author();
        Author authorB = new Author();
        
        authorA.setId(1L);
        authorA.setFirstName("Jan");
        authorA.setLastName("Kowalski");
 
        authorB.setId(2L);
        authorB.setFirstName("Paulo");
        authorB.setLastName("Coelho");
        
        Set<Author> authors = Set.of(authorA, authorB);
        
        Book newBook = new Book();
        newBook.setAuthors(authors);
        newBook.setDescription(description);
        newBook.setKeywords(bookKeywords);
        
        BookWithKeywordsDto newBookDto = new BookWithKeywordsDto();
        newBookDto.setId(BOOK_ID);
        newBookDto.setAuthorsIds(authorsIds);
        newBookDto.setDescription(description);
        newBookDto.setKeywords(bookKeywordsString);
        
        when(keywordRepository.findAllByNameIn(any())).thenReturn(bookKeywordsAlreadyPresent);
        when(bookRepository.save(any(Book.class))).thenReturn(newBook);
        when(bookMapper.mapToDtoWithKeywords(any(Book.class))).thenReturn(newBookDto);
        
        BookWithKeywordsDto result = bookService.save(bookDto);
        
        assertThat(result)
                .isNotNull()
                .extracting("id", "authorsIds", "description", "keywords")
                .containsExactly(BOOK_ID, authorsIds, description, bookKeywordsString);
        
        verify(keywordRepository).findAllByNameIn(eq(bookKeywordNames));
        verify(bookRepository).save(eq(newBook));
        verify(bookMapper).mapToDtoWithKeywords(eq(newBook));
        verifyNoInteractions(bookHistoryRepository, bookCopyRepository);
    }
    
//    @Test
//    void updateBook() {
//        String description = "A fantastic book about the power of friendship.";
//        String keywordsString = "friendship, book, adventure, power, friendship";
//        Set<Long> authorsIds = Set.of(1L, 2L, 5L);
//
//        BookWithKeywordsDto bookDto = new BookWithKeywordsDto();
//        bookDto.setId(BOOK_ID);
//        bookDto.setAuthorsIds(authorsIds);
//        bookDto.setDescription(description);
//        bookDto.setKeywords(keywordsString);
//
//        Set<Keyword> keywords = new LinkedHashSet<>();
//        keywords.add(new Keyword("friendship"));
//        keywords.add(new Keyword("book"));
//        keywords.add(new Keyword("adventure"));
//        keywords.add(new Keyword("power"));
//
//        String mappedKeywords = "friendship, book, adventure, power";
//
//        Set<Author> authors = new LinkedHashSet<>();
//        authors.add(new Author(1L));
//        authors.add(new Author(2L));
//        authors.add(new Author(5L));
//
//        Book newBook = new Book();
//        newBook.setAuthors(authors);
//        newBook.setDescription(description);
//        newBook.setKeywords(keywords);
//        
//        Set<Long> authorsIds = 
//        
//        Book oldBook = new Book();
//        oldBook.setAuthors();
//
//        BookWithKeywordsDto savedBookDto = new BookWithKeywordsDto();
//        savedBookDto.setId(BOOK_ID);
//        savedBookDto.setAuthorsIds(authorsIds);
//        savedBookDto.setDescription(description);
//        savedBookDto.setKeywords("friendship, book, adventure, power");
//
//        when(bookRepository.save(any(Book.class))).thenReturn(newBook);
//        when(bookMapper.mapToDtoWithKeywords(any(Book.class))).thenReturn(savedBookDto);
//
//        BookWithKeywordsDto result = bookService.save(bookDto);
//
//        assertThat(result)
//                .isNotNull()
//                .extracting("id", "authorsIds", "description", "keywords")
//                .containsExactly(BOOK_ID, authorsIds, description, mappedKeywords);
//
//        verify(bookRepository).save(newBook);
//        verify(bookMapper).mapToDtoWithKeywords(newBook);
//        verifyNoInteractions(bookHistoryRepository, bookCopyRepository, keywordRepository);
//    }
    
    
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
        verifyNoInteractions(bookHistoryRepository, bookCopyRepository, keywordRepository);
    }

    @Test
    void getBookWithKeywordsById_InvalidId() {
        String expectedMessage = "Unable to fetch book with given id: " + BOOK_ID;

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookWithKeywordsById(BOOK_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);

        verify(bookRepository).findById(eq(BOOK_ID));
        verifyNoInteractions(bookHistoryRepository, bookCopyRepository, bookMapper, keywordRepository);
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
        verifyNoInteractions(keywordRepository);
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
        verifyNoInteractions(bookHistoryRepository, keywordRepository);
    }
}
