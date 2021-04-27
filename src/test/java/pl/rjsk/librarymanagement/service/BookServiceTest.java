package pl.rjsk.librarymanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rjsk.librarymanagement.mapper.BookMapper;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.repository.BookHistoryRepository;
import pl.rjsk.librarymanagement.model.entity.BookCopy;
import pl.rjsk.librarymanagement.repository.BookCopyRepository;
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

    @InjectMocks
    private BookService bookService;

    @Test
    void getAllBooksToDisplay() {
        List<Book> books = Collections.emptyList();

        var bookDto = new BookDto();
        bookDto.setId(BOOK_ID);

        var bookCopy = new BookCopy();
        bookCopy.setId(BOOK_COPY_ID);

        List<Long> bookInstanceIds = List.of(BOOK_ID);

        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.mapAsList(anyCollection())).thenReturn(List.of(bookDto));
        when(bookCopyRepository.findAllByBookId(anyLong())).thenReturn(List.of(bookCopy));
        when(bookHistoryRepository.findAllNotAvailable(anyCollection())).thenReturn(Collections.emptyList());

        List<BookDto> result = bookService.getAllBooksToDisplay();

        assertThat(result)
                .hasSize(1)
                .extracting("id", "numberOfAvailableCopies")
                .containsExactly(tuple(BOOK_ID, NUM_OF_COPIES));

        verify(bookRepository).findAll();
        verify(bookMapper).mapAsList(eq(books));
        verify(bookCopyRepository).findAllByBookId(eq(BOOK_ID));
        verify(bookHistoryRepository).findAllNotAvailable(eq(bookInstanceIds));
    }
}
