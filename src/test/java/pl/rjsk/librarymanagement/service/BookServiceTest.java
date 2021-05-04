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
import pl.rjsk.librarymanagement.mapper.BookMapper;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.dto.BookWithCopiesDto;
import pl.rjsk.librarymanagement.model.dto.BookWithKeywordsDto;
import pl.rjsk.librarymanagement.model.entity.*;
import pl.rjsk.librarymanagement.repository.BookCopyRepository;
import pl.rjsk.librarymanagement.repository.BookHistoryRepository;
import pl.rjsk.librarymanagement.repository.BookRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
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

    @InjectMocks
    private BookService bookService;

    @Test
    void getBookWithKeywordsById() {
        var authors = Set.of(
            new Author(1),
            new Author(2));
        var authorsIds = Set.of(1L, 2L);

        var keywords = Set.of(
                new Keyword("history"),
                new Keyword("war"));
        String allKeywordsA = "history, war";
        String allKeywordsB = "war, history";

        Book book = new Book();
        book.setId(BOOK_ID);
        book.setKeywords(keywords);
        book.setAuthors(authors);

        BookWithKeywordsDto bookDto = new BookWithKeywordsDto();
        bookDto.setId(BOOK_ID);

        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(bookMapper.mapToDtoWithKeywords(book)).thenReturn(bookDto);

        var result = bookService.getBookWithKeywordsById(BOOK_ID);

        assertThat(result)
                .extracting("id", "authorsIds", "keywords")
                .contains(BOOK_ID, authorsIds)
                .containsAnyElementsOf(List.of(allKeywordsA, allKeywordsB));

        verify(bookRepository).findById(eq(BOOK_ID));
        verify(bookMapper).mapToDtoWithKeywords(eq(book));
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
        verifyNoInteractions(bookHistoryRepository);
    }
}
