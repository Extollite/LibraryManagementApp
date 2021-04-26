package pl.rjsk.librarymanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rjsk.librarymanagement.mapper.BookMapper;
import pl.rjsk.librarymanagement.model.dto.BookDisplayDto;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.BookInstance;
import pl.rjsk.librarymanagement.repository.BookHistoryRepository;
import pl.rjsk.librarymanagement.repository.BookInstanceRepository;
import pl.rjsk.librarymanagement.repository.BookRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    private static final long BOOK_ID = 1L;
    private static final long BOOK_INSTANCE_ID = 1L;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookInstanceRepository bookInstanceRepository;

    @Mock
    private BookHistoryRepository bookHistoryRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAllBooksToDisplay() {
        List<Book> books = Collections.emptyList();

        var bookDisplay = new BookDisplayDto();
        bookDisplay.setId(BOOK_ID);

        var bookInstance = new BookInstance();
        bookInstance.setId(BOOK_INSTANCE_ID);

        List<Long> bookInstanceIds = List.of(BOOK_INSTANCE_ID);

        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.mapAsList(anyCollection())).thenReturn(List.of(bookDisplay));
        when(bookInstanceRepository.findAllByBookId(anyLong())).thenReturn(List.of(bookInstance));
        when(bookHistoryRepository.findAllNotAvailable(anyCollection())).thenReturn(Collections.emptyList());

        List<BookDisplayDto> result = bookService.getAllBooksToDisplay();

        assertThat(result)
                .hasSize(1)
                .extracting("id", "bookInstanceIds")
                .containsExactly(tuple(BOOK_ID, bookInstanceIds));

        verify(bookRepository).findAll();
        verify(bookMapper).mapAsList(eq(books));
        verify(bookInstanceRepository).findAllByBookId(eq(BOOK_ID));
        verify(bookHistoryRepository).findAllNotAvailable(eq(bookInstanceIds));
    }
}