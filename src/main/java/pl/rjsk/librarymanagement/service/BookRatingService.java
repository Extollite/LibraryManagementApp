package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookRatingService {

    private final BookRatingRepository bookRatingRepository;
    private final BookRatingMapper bookRatingMapper;
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;


    @Transactional
    public BookRatingDto updateOrSave(User user, long bookId, int rating) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Unable to fetch book with given id: " + bookId));
        if (rating < 1 || 10 < rating) {
            throw new IllegalArgumentException("Book rating must be within 1-10 range. Rating " + rating + " is not");
        }

        BookRating bookRating = bookRatingRepository.findBookRatingByUserAndBook(user, book)
                .map(br -> br.setRating(rating))
                .orElse(save(user, book, rating));

        return bookRatingMapper.mapToDto(bookRating);
    }

    private BookRating save(User user, Book book, int rating) {
        BookRating bookRating = new BookRating();
        bookRating.setBook(book);
        bookRating.setUser(user);
        bookRating.setRating(rating);

        return bookRatingRepository.save(bookRating);
    }

    public BookRatingDto get(User user, long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Unable to fetch book with given id: " + bookId));

        BookRating bookRating = bookRatingRepository.findBookRatingByUserAndBook(user, book)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unable to fetch rating for userId: " + user.getId() + ", bookId: " + bookId));

        return bookRatingMapper.mapToDto(bookRating);
    }

    public List<BookWithRatingDto> getAll(User user) {
        List<BookRating> bookRatings = bookRatingRepository.findAllByUser(user);
        return bookRatings.stream()
                .map(br -> {
                    BookWithRatingDto bookWithRatingDto = bookMapper.mapToBookWithRating(br.getBook());
                    bookWithRatingDto.setRating(br.getRating());
                    return bookWithRatingDto;
                })
                .collect(Collectors.toList());
    }
}
