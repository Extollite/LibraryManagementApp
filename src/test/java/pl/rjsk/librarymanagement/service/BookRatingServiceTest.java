package pl.rjsk.librarymanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.BookRating;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.repository.BookRatingRepository;
import pl.rjsk.librarymanagement.repository.BookRepository;
import pl.rjsk.librarymanagement.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookRatingServiceTest {

    private static final long BOOK_RATING_ID = 1L;
    private static final long USER_ID = 2L;
    private static final long BOOK_ID = 3L;

    @Mock
    private BookRatingRepository bookRatingRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookRatingService bookRatingService;

    @Test
    void updateOrSave_userException() {
        int rating = 5;

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookRatingService.updateOrSave(USER_ID, BOOK_ID, rating))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unable to fetch user with given id: " + USER_ID);

        verify(userRepository).findById(eq(USER_ID));
    }

    @Test
    void updateOrSave_bookException() {
        int rating = 5;
        User user = new User();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookRatingService.updateOrSave(USER_ID, BOOK_ID, rating))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unable to fetch book with given id: " + BOOK_ID);

        verify(userRepository).findById(eq(USER_ID));
        verify(bookRepository).findById(eq(BOOK_ID));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 11, 12})
    void updateOrSave_ratingTooLowException(int rating) {
        User user = new User();
        Book book = new Book();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookRatingService.updateOrSave(USER_ID, BOOK_ID, rating))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book rating must be within 1-10 range. Rating " + rating + " is not");

        verify(userRepository).findById(eq(USER_ID));
        verify(bookRepository).findById(eq(BOOK_ID));
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

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookRatingRepository.findBookRatingByBookAndUser(any(Book.class), any(User.class)))
                .thenReturn(Optional.of(bookRating));

        BookRating result = bookRatingService.updateOrSave(USER_ID, BOOK_ID, newRating);

        assertThat(result)
                .isNotNull()
                .extracting("user", "book", "rating")
                .containsExactly(user, book, newRating);

        verify(userRepository).findById(eq(USER_ID));
        verify(bookRepository).findById(eq(BOOK_ID));
        verify(bookRatingRepository).findBookRatingByBookAndUser(eq(book), eq(user));
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

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookRatingRepository.findBookRatingByBookAndUser(any(Book.class), any(User.class)))
                .thenReturn(Optional.empty());
        when(bookRatingRepository.save(any(BookRating.class))).thenReturn(bookRating);

        BookRating result = bookRatingService.updateOrSave(USER_ID, BOOK_ID, rating);

        assertThat(result)
                .isNotNull()
                .extracting("user", "book", "rating")
                .containsExactly(user, book, rating);

        verify(userRepository).findById(eq(USER_ID));
        verify(bookRepository).findById(eq(BOOK_ID));
        verify(bookRatingRepository).findBookRatingByBookAndUser(eq(book), eq(user));
        verify(bookRatingRepository).save(eq(bookRating));
    }

    @Test
    void testGetAll_userException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookRatingService.getAll(USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unable to fetch user with given id: " + USER_ID);

        verify(userRepository).findById(eq(USER_ID));
    }

    @Test
    void testGetAll() {
        User user = new User();
        user.setId(USER_ID);

        User otherUser = new User();
        otherUser.setId(USER_ID + 1);

        BookRating bookRatingByUser = new BookRating();
        bookRatingByUser.setUser(user);
        
        BookRating bookRatingByOtherUser = new BookRating();
        bookRatingByOtherUser.setUser(otherUser);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bookRatingRepository.findAll())
                .thenReturn(List.of(bookRatingByUser, bookRatingByOtherUser));

        List<BookRating> result = bookRatingService.getAll(USER_ID);

        assertThat(result)
                .hasSize(1)
                .extracting("user")
                .containsExactly(user);


        verify(bookRatingRepository).findAll();
    }
}
