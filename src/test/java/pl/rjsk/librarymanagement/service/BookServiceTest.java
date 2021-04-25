package pl.rjsk.librarymanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rjsk.librarymanagement.mapper.BookMapper;
import pl.rjsk.librarymanagement.model.dto.BookDisplayDto;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.BookRepresentation;
import pl.rjsk.librarymanagement.repository.BookRepository;
import pl.rjsk.librarymanagement.repository.BookRepresentationRepository;

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
    private static final long BOOK_REPRESENTATION_ID = 1L;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookRepresentationRepository bookRepresentationRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAllBooksToDisplay() {
        List<Book> books = Collections.emptyList();

        var bookDisplay = new BookDisplayDto();
        bookDisplay.setId(BOOK_ID);

        var bookRepresentation = new BookRepresentation();
        bookRepresentation.setId(BOOK_REPRESENTATION_ID);

        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.mapAsList(anyCollection())).thenReturn(List.of(bookDisplay));
        when(bookRepresentationRepository.findAllByBookId(anyLong())).thenReturn(List.of(bookRepresentation));

        List<BookDisplayDto> result = bookService.getAllBooksToDisplay();

        assertThat(result)
                .hasSize(1)
                .extracting("id", "bookRepresentationIds")
                .containsExactly(tuple(BOOK_ID, List.of(BOOK_REPRESENTATION_ID)));

        verify(bookRepository).findAll();
        verify(bookMapper).mapAsList(eq(books));
        verify(bookRepresentationRepository).findAllByBookId(eq(BOOK_ID));
    }
}