package pl.rjsk.librarymanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rjsk.librarymanagement.exception.IncorrectDataException;
import pl.rjsk.librarymanagement.exception.ResourceNotFoundException;
import pl.rjsk.librarymanagement.mapper.BookMapper;
import pl.rjsk.librarymanagement.mapper.BookRatingMapper;
import pl.rjsk.librarymanagement.model.dto.BookRatingDto;
import pl.rjsk.librarymanagement.model.dto.BookWithRatingDto;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.BookRating;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.repository.BookRatingRepository;
import pl.rjsk.librarymanagement.repository.BookRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookRatingServiceTest {

    private static final long BOOK_RATING_ID = 1L;
    private static final long USER_ID = 2L;
    private static final long BOOK_ID = 3L;

    @Mock
    private BookRatingRepository bookRatingRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookRatingMapper bookRatingMapper;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookRatingService bookRatingService;

    @Test
    void updateOrSave_bookException() {
        int rating = 5;
        User user = new User();

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookRatingService.updateOrSave(user, BOOK_ID, rating))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Unable to fetch book with given id: " + BOOK_ID);

        verify(bookRepository).findById(eq(BOOK_ID));
        verifyNoInteractions(bookMapper);
        verifyNoInteractions(bookRatingMapper);
        verifyNoInteractions(bookRatingRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 11, 12})
    void updateOrSave_ratingValueException(int rating) {
        User user = new User();
        Book book = new Book();

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookRatingService.updateOrSave(user, BOOK_ID, rating))
                .isInstanceOf(IncorrectDataException.class)
                .hasMessage("Book rating must be within 1-10 range. Rating " + rating + " is not");

        verify(bookRepository).findById(eq(BOOK_ID));
        verifyNoInteractions(bookMapper);
        verifyNoInteractions(bookRatingMapper);
        verifyNoInteractions(bookRatingRepository);
    }

    @Test
    void updateOrSave_update() {
        User user = new User();
        user.setId(USER_ID);

        Book book = new Book();
        book.setId(BOOK_ID);

        int oldRating = 5;
        int newRating = 7;

        BookRating bookRating = new BookRating();
        bookRating.setId(BOOK_RATING_ID);
        bookRating.setUser(user);
        bookRating.setBook(book);
        bookRating.setRating(oldRating);

        BookRating newBookRating = new BookRating();
        newBookRating.setId(BOOK_RATING_ID);
        newBookRating.setUser(user);
        newBookRating.setBook(book);
        newBookRating.setRating(newRating);

        BookRatingDto bookRatingDto = new BookRatingDto();
        bookRatingDto.setId(BOOK_RATING_ID);
        bookRatingDto.setUserId(USER_ID);
        bookRatingDto.setBookId(BOOK_ID);
        bookRatingDto.setRating(newRating);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookRatingRepository.findBookRatingByUserAndBook(any(User.class), any(Book.class)))
                .thenReturn(Optional.of(bookRating));
        when(bookRatingMapper.mapToDto(any(BookRating.class))).thenReturn(bookRatingDto);

        BookRatingDto result = bookRatingService.updateOrSave(user, BOOK_ID, newRating);

        assertThat(result)
                .isNotNull()
                .extracting("userId", "bookId", "rating")
                .containsExactly(USER_ID, BOOK_ID, newRating);

        verify(bookRepository).findById(eq(BOOK_ID));
        verify(bookRatingRepository).findBookRatingByUserAndBook(eq(user), eq(book));
        verify(bookRatingMapper).mapToDto(eq(newBookRating));
        verifyNoInteractions(bookMapper);
    }

    @Test
    void updateOrSave_save() {
        User user = new User();
        user.setId(USER_ID);

        Book book = new Book();
        book.setId(BOOK_ID);

        int rating = 5;

        BookRating bookRating = new BookRating();
        bookRating.setUser(user);
        bookRating.setBook(book);
        bookRating.setRating(rating);

        BookRatingDto bookRatingDto = new BookRatingDto();
        bookRatingDto.setUserId(USER_ID);
        bookRatingDto.setBookId(BOOK_ID);
        bookRatingDto.setRating(rating);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookRatingRepository.findBookRatingByUserAndBook(any(User.class), any(Book.class)))
                .thenReturn(Optional.empty());
        when(bookRatingRepository.save(any(BookRating.class))).thenReturn(bookRating);
        when(bookRatingMapper.mapToDto(any(BookRating.class))).thenReturn(bookRatingDto);

        BookRatingDto result = bookRatingService.updateOrSave(user, BOOK_ID, rating);

        assertThat(result)
                .isNotNull()
                .extracting("userId", "bookId", "rating")
                .containsExactly(USER_ID, BOOK_ID, rating);

        verify(bookRepository).findById(eq(BOOK_ID));
        verify(bookRatingRepository).findBookRatingByUserAndBook(eq(user), eq(book));
        verify(bookRatingRepository).save(eq(bookRating));
        verifyNoInteractions(bookMapper);
    }

    @Test
    void get_bookException() {
        User user = new User();

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookRatingService.get(user, BOOK_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Unable to fetch book with given id: " + BOOK_ID);

        verify(bookRepository).findById(eq(BOOK_ID));
        verifyNoInteractions(bookMapper);
        verifyNoInteractions(bookRatingMapper);
        verifyNoInteractions(bookRatingRepository);
    }

    @Test
    void get_noRatingYet() {
        User user = new User();
        user.setId(USER_ID);

        Book book = new Book();
        book.setId(BOOK_ID);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookRatingRepository.findBookRatingByUserAndBook(any(User.class), any(Book.class)))
                .thenReturn(Optional.empty());

        BookRatingDto result = bookRatingService.get(user, BOOK_ID);

        assertThat(result)
                .isNull();

        verify(bookRepository).findById(eq(BOOK_ID));
        verify(bookRatingRepository).findBookRatingByUserAndBook(eq(user), eq(book));
        verifyNoInteractions(bookMapper);
        verifyNoInteractions(bookRatingMapper);
    }

    @Test
    void get() {
        User user = new User();
        user.setId(USER_ID);

        Book book = new Book();
        book.setId(BOOK_ID);

        int rating = 3;

        BookRating bookRating = new BookRating();
        bookRating.setUser(user);
        bookRating.setBook(book);
        bookRating.setRating(rating);

        BookRatingDto bookRatingDto = new BookRatingDto();
        bookRatingDto.setUserId(USER_ID);
        bookRatingDto.setBookId(BOOK_ID);
        bookRatingDto.setRating(rating);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookRatingRepository.findBookRatingByUserAndBook(any(User.class), any(Book.class)))
                .thenReturn(Optional.of(bookRating));
        when(bookRatingMapper.mapToDto(any(BookRating.class))).thenReturn(bookRatingDto);

        BookRatingDto result = bookRatingService.get(user, BOOK_ID);

        assertThat(result)
                .isNotNull()
                .extracting("userId", "bookId", "rating")
                .containsExactly(USER_ID, BOOK_ID, rating);

        verify(bookRepository).findById(eq(BOOK_ID));
        verify(bookRatingRepository).findBookRatingByUserAndBook(eq(user), eq(book));
        verify(bookRatingMapper).mapToDto(eq(bookRating));
        verifyNoInteractions(bookMapper);
    }

    @Test
    void getAll() {
        int rating = 9;

        User user = new User();
        user.setId(USER_ID);

        Book book = new Book();
        book.setId(BOOK_ID);

        BookRating bookRatingByUser = new BookRating();
        bookRatingByUser.setUser(user);
        bookRatingByUser.setBook(book);
        bookRatingByUser.setRating(rating);

        BookWithRatingDto bookWithRatingDto = new BookWithRatingDto();
        bookWithRatingDto.setId(BOOK_ID);
        bookWithRatingDto.setRating(rating);

        when(bookRatingRepository.findAllByUser(any(User.class))).thenReturn(List.of(bookRatingByUser));
        when(bookMapper.mapToBookWithRating(any(Book.class))).thenReturn(bookWithRatingDto);

        List<BookWithRatingDto> result = bookRatingService.getAll(user);

        assertThat(result)
                .hasSize(1)
                .extracting("id", "rating")
                .containsExactly(tuple(BOOK_ID, rating));

        verify(bookRatingRepository).findAllByUser(eq(user));
        verify(bookMapper).mapToBookWithRating(eq(book));
        verifyNoInteractions(bookRepository);
        verifyNoInteractions(bookRatingMapper);
    }
}
