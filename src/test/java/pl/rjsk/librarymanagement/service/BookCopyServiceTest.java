package pl.rjsk.librarymanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rjsk.librarymanagement.mapper.BookCopyMapper;
import pl.rjsk.librarymanagement.model.dto.BookCopyDueDateDto;
import pl.rjsk.librarymanagement.model.entity.BookCopy;
import pl.rjsk.librarymanagement.repository.BookCopyRepository;
import pl.rjsk.librarymanagement.repository.BookHistoryRepository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookCopyServiceTest {

    private static final long BOOK_ID = 1L;
    private static final long BOOK_COPY_AVAILABLE_ID = 2L;
    private static final long BOOK_COPY_UNAVAILABLE_ID = 3L;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private BookHistoryRepository bookHistoryRepository;

    @Mock
    private BookCopyMapper bookCopyMapper;

    @InjectMocks
    private BookCopyService bookCopyService;

    @Test
    void getAllByBookId() {
        var bookCopyAvailable = new BookCopy();
        bookCopyAvailable.setId(BOOK_COPY_AVAILABLE_ID);

        var bookCopyUnavailable = new BookCopy();
        bookCopyUnavailable.setId(BOOK_COPY_UNAVAILABLE_ID);

        var bookCopyDueDateAvailable = new BookCopyDueDateDto();
        bookCopyDueDateAvailable.setBookId(BOOK_ID);
        bookCopyDueDateAvailable.setId(BOOK_COPY_AVAILABLE_ID);

        var bookCopyDueDateUnavailable = new BookCopyDueDateDto();
        bookCopyDueDateUnavailable.setBookId(BOOK_ID);
        bookCopyDueDateUnavailable.setId(BOOK_COPY_UNAVAILABLE_ID);
        var dueDate = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);

        var bookCopies = List.of(bookCopyAvailable, bookCopyUnavailable);

        when(bookCopyRepository.findAllByBookId(BOOK_ID)).thenReturn(bookCopies);
        when(bookCopyMapper.mapAsList(anyCollection()))
                .thenReturn(List.of(bookCopyDueDateAvailable, bookCopyDueDateUnavailable));
        when(bookHistoryRepository.findDueDate(BOOK_COPY_AVAILABLE_ID)).thenReturn(Optional.empty());
        when(bookHistoryRepository.findDueDate(BOOK_COPY_UNAVAILABLE_ID)).thenReturn(Optional.of(dueDate));

        List<BookCopyDueDateDto> result = bookCopyService.getAllByBookId(BOOK_ID);

        assertThat(result)
                .hasSize(2)
                .extracting("id", "bookId", "available")
                .containsExactly(
                        tuple(BOOK_COPY_AVAILABLE_ID, BOOK_ID, true),
                        tuple(BOOK_COPY_UNAVAILABLE_ID, BOOK_ID, false));

        verify(bookCopyRepository).findAllByBookId(eq(BOOK_ID));
        verify(bookCopyMapper).mapAsList(eq(bookCopies));
        verify(bookHistoryRepository).findDueDate(eq(BOOK_COPY_AVAILABLE_ID));
        verify(bookHistoryRepository).findDueDate(eq(BOOK_COPY_UNAVAILABLE_ID));
    }

    @Test
    void getByCopyId() {
        var bookCopyDto = new BookCopyDueDateDto();
        bookCopyDto.setId(BOOK_COPY_AVAILABLE_ID);
        var bookCopy = new BookCopy();

        when(bookCopyRepository.findById(BOOK_COPY_AVAILABLE_ID)).thenReturn(Optional.of(bookCopy));
        when(bookCopyMapper.mapToDto(any(BookCopy.class))).thenReturn(bookCopyDto);
        when(bookHistoryRepository.findDueDate(anyLong())).thenReturn(Optional.empty());

        BookCopyDueDateDto result = bookCopyService.getByCopyId(BOOK_COPY_AVAILABLE_ID);

        assertThat(result)
                .isNotNull()
                .extracting("available")
                .isEqualTo(Boolean.TRUE);

        verify(bookCopyRepository).findById(eq(BOOK_COPY_AVAILABLE_ID));
        verify(bookCopyMapper).mapToDto(eq(bookCopy));
        verify(bookHistoryRepository).findDueDate(eq(BOOK_COPY_AVAILABLE_ID));
    }

    @Test
    void getByCopyId_exceptionThrown() {
        when(bookCopyRepository.findById(BOOK_COPY_AVAILABLE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookCopyService.getByCopyId(BOOK_COPY_AVAILABLE_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unable to fetch book copy with given id: " + BOOK_COPY_AVAILABLE_ID);

        verify(bookCopyRepository).findById(eq(BOOK_COPY_AVAILABLE_ID));
        verifyNoInteractions(bookCopyMapper, bookHistoryRepository);
    }

    @Test
    void saveBookCopy() {
        var bookCopyDto = new BookCopyDueDateDto();
        bookCopyDto.setAlternativeTitle("");

        var bookCopy = new BookCopy();

        when(bookCopyMapper.mapToEntity(any(BookCopyDueDateDto.class))).thenReturn(bookCopy);
        when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(bookCopy);
        when(bookCopyMapper.mapToDto(any(BookCopy.class))).thenReturn(bookCopyDto);

        BookCopyDueDateDto result = bookCopyService.saveBookCopy(bookCopyDto);

        assertThat(result)
                .isNotNull()
                .extracting("alternativeTitle")
                .isNull();

        verify(bookCopyMapper).mapToEntity(eq(bookCopyDto));
        verify(bookCopyRepository).save(eq(bookCopy));
        verify(bookCopyMapper).mapToDto(eq(bookCopy));
        verifyNoInteractions(bookHistoryRepository);
    }

    @Test
    void updateBook() {
        var bookCopyDto = new BookCopyDueDateDto();
        bookCopyDto.setId(BOOK_COPY_AVAILABLE_ID);
        bookCopyDto.setAlternativeTitle("");
        var bookCopy = new BookCopy();
        bookCopy.setAlternativeTitle("title");

        when(bookCopyRepository.findById(BOOK_COPY_AVAILABLE_ID)).thenReturn(Optional.of(bookCopy));
        when(bookCopyMapper.mapToDto(any(BookCopy.class))).thenReturn(bookCopyDto);
        when(bookHistoryRepository.findDueDate(anyLong())).thenReturn(Optional.empty());

        BookCopyDueDateDto result = bookCopyService.getByCopyId(BOOK_COPY_AVAILABLE_ID);

        assertThat(result)
                .isNotNull()
                .extracting("available")
                .isEqualTo(Boolean.TRUE);

        verify(bookCopyRepository).findById(eq(BOOK_COPY_AVAILABLE_ID));
        verify(bookCopyMapper).mapToDto(eq(bookCopy));
        verify(bookHistoryRepository).findDueDate(eq(BOOK_COPY_AVAILABLE_ID));
    }
}
