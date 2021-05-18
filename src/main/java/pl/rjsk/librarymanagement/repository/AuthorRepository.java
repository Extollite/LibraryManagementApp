package pl.rjsk.librarymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rjsk.librarymanagement.model.entity.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
}
