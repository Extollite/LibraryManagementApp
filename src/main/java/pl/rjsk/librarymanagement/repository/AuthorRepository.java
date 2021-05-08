package pl.rjsk.librarymanagement.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pl.rjsk.librarymanagement.model.entity.Author;

import java.util.List;

@Repository
public interface AuthorRepository extends PagingAndSortingRepository<Author, Long> {

    @Override
    List<Author> findAll();
}
