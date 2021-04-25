package pl.rjsk.librarymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rjsk.librarymanagement.model.entity.BookInstance;

import java.util.List;

@Repository
public interface BookInstanceRepository extends JpaRepository<BookInstance, Long> {

    List<BookInstance> findAllByBookId(long bookId);
}
