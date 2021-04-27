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
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookCopyServiceTest {
    
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
        
        verify(bookCopyRepository, times(1)).findAllByBookId(BOOK_ID);
        verify(bookCopyMapper, times(1)).mapAsList(eq(bookCopies));
        verify(bookHistoryRepository, times(1)).findDueDate(BOOK_COPY_AVAILABLE_ID);
        verify(bookHistoryRepository, times(1)).findDueDate(BOOK_COPY_UNAVAILABLE_ID);
    }
}
