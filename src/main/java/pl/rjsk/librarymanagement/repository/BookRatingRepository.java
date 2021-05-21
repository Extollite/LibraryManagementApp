package pl.rjsk.librarymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.BookRating;
import pl.rjsk.librarymanagement.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface BookRatingRepository extends JpaRepository<BookRating, Long> {

    List<BookRating> findAllByUser(User user);

    Optional<BookRating> findBookRatingByUserAndBook(User user, Book book);
}
