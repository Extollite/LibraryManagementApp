package pl.rjsk.librarymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rjsk.librarymanagement.model.entity.BookRepresentation;

import java.util.List;

@Repository
public interface BookRepresentationRepository extends JpaRepository<BookRepresentation, Long> {

    List<BookRepresentation> findAllByBookId(long bookId);
}
