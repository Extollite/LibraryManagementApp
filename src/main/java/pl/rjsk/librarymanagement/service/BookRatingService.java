package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.BookRating;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.repository.BookRatingRepository;
import pl.rjsk.librarymanagement.repository.BookRepository;
import pl.rjsk.librarymanagement.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookRatingService {

    private final BookRatingRepository bookRatingRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookRating updateOrSave(long userId, long bookId, int rating) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unable to fetch user with given id: " + userId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Unable to fetch book with given id: " + bookId));
        if (rating < 1 || 10 < rating) {
            throw new IllegalArgumentException("Book rating must be within 1-10 range. Rating " + rating + " is not");
        }

        BookRating bookRating;
        Optional<BookRating> bookRatingOptional = bookRatingRepository.findBookRatingByBookAndUser(book, user);

        if (bookRatingOptional.isPresent()) {
            bookRating = bookRatingOptional.get();
            bookRating.setRating(rating);

            return bookRating;
        }

        bookRating = new BookRating();
        bookRating.setUser(user);
        bookRating.setBook(book);
        bookRating.setRating(rating);
        return bookRatingRepository.save(bookRating);
    }

    public List<BookRating> getAll(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unable to fetch user with given id: " + userId));

        List<BookRating> allRatings = bookRatingRepository.findAll();
        return allRatings.stream()
                .filter(br -> br.getUser().equals(user))
                .collect(Collectors.toList());
    }
}
