package pl.rjsk.librarymanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.rjsk.librarymanagement.exception.ResourceNotFoundException;
import pl.rjsk.librarymanagement.mapper.BookMapper;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.dto.BookWithCopiesDto;
import pl.rjsk.librarymanagement.model.dto.BookWithKeywordsDto;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.BookCopy;
import pl.rjsk.librarymanagement.repository.BookCopyRepository;
import pl.rjsk.librarymanagement.repository.BookHistoryRepository;
import pl.rjsk.librarymanagement.repository.BookRepository;
import pl.rjsk.librarymanagement.repository.KeywordRepository;

import java.util.Collections;
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
                .isInstanceOf(ResourceNotFoundException.class)
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
