package pl.rjsk.librarymanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.BookRating;
import pl.rjsk.librarymanagement.model.entity.BookRecommendation;
import pl.rjsk.librarymanagement.model.entity.Keyword;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.repository.BookRatingRepository;
import pl.rjsk.librarymanagement.repository.BookRecommendationRepository;
import pl.rjsk.librarymanagement.repository.BookRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookRecommendationServiceTest {

    @Mock
    private BookRatingRepository bookRatingRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookRecommendationRepository bookRecommendationRepository;

    @InjectMocks
    private BookRecommendationService bookRecommendationService;

    @Test
    void recalculateRecommendations_notEnoughRatedBooks() {
        var bookRating = new BookRating();
        var user = new User();

        when(bookRatingRepository.findAllByUser(any(User.class)))
                .thenReturn(List.of(bookRating, bookRating));

        List<BookRecommendation> result = bookRecommendationService.recalculateRecommendations(user);

        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(bookRatingRepository).findAllByUser(eq(user));
        verifyNoInteractions(bookRepository, bookRecommendationRepository);
    }

    @Test
    void recalculateRecommendations() {
        final User user = new User();

        final long bookRating1stId = 1L;
        BookRating bookRatingEmptyKeywords = prepareBookRating(bookRating1stId, Collections.emptySet(), 10);

        final long bookRating2ndId = 2L;
        var bookRating2nd = prepareBookRating(bookRating2ndId,
                Set.of(new Keyword("1"), new Keyword("2"), new Keyword("3")), 8);

        final long bookRating3rdId = 3L;
        var bookRating3rd = prepareBookRating(bookRating3rdId, Set.of(new Keyword("2"), new Keyword("3"), new Keyword("4"),
                new Keyword("5")), 7);

        final String book1stTitle = "first";
        var book1st = prepareBook(book1stTitle,
                Set.of(new Keyword("1"), new Keyword("4"), new Keyword("7"), new Keyword("8"), new Keyword("9")));

        final String book2ndTitle = "second";
        var book2nd = prepareBook(book2ndTitle,
                Set.of(new Keyword("2"), new Keyword("3"), new Keyword("7")));

        final String book3dTitle = "third";
        var book3rd = prepareBook(book3dTitle, Set.of(new Keyword("1")));

        final String book4thTitle = "fourth";
        var book4th = prepareBook(book4thTitle, Set.of(new Keyword("9")));

        when(bookRatingRepository.findAllByUser(any(User.class)))
                .thenReturn(List.of(bookRatingEmptyKeywords, bookRating2nd, bookRating3rd));
        when(bookRepository.findAllByIdNotIn(anyCollection())).thenReturn(List.of(book1st, book2nd, book3rd, book4th));
        when(bookRecommendationRepository.saveAll(anyIterable())).thenAnswer(invocation -> invocation.getArgument(0));

        List<BookRecommendation> result = bookRecommendationService.recalculateRecommendations(user);

        assertThat(result)
                .isNotNull()
                .hasSize(3)
                .extracting("book")
                .extracting("title")
                .containsExactly(book2ndTitle, book3dTitle, book1stTitle);

        verify(bookRatingRepository).findAllByUser(eq(user));
        verify(bookRecommendationRepository).deleteAllByUser(eq(user));
        verify(bookRepository).findAllByIdNotIn(eq(Set.of(bookRating1stId, bookRating2ndId, bookRating3rdId)));
        verify(bookRecommendationRepository).saveAll(anyList());
    }

    private BookRating prepareBookRating(long bookId, Set<Keyword> keywords, int rating) {
        var book = new Book();
        book.setId(bookId);
        book.setKeywords(keywords);

        var bookRating = new BookRating();
        bookRating.setBook(book);
        bookRating.setRating(rating);

        return bookRating;
    }

    private Book prepareBook(String title, Set<Keyword> keywords) {
        var book = new Book();
        book.setTitle(title);
        book.setKeywords(keywords);

        return book;
    }
}
