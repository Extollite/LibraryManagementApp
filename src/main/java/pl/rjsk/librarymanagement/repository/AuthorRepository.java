package pl.rjsk.librarymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rjsk.librarymanagement.model.entity.Author;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Override
    List<Author> findAll();

    Optional<Author> findAuthorByFirstNameAndLastName(String firstName, String lastName);
}
