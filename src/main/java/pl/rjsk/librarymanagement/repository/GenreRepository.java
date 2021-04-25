package pl.rjsk.librarymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rjsk.librarymanagement.model.entity.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

}
