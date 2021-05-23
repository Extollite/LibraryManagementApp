package pl.rjsk.librarymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rjsk.librarymanagement.model.entity.BookRecommendation;
import pl.rjsk.librarymanagement.model.entity.User;

import java.util.List;

public interface BookRecommendationRepository extends JpaRepository<BookRecommendation, Long> {

    List<BookRecommendation> getAllByUser(User user);

    void deleteAllByUser(User user);
}
