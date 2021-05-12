package pl.rjsk.librarymanagement.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pl.rjsk.librarymanagement.model.entity.Book;

import java.util.List;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {

    List<Book> findAllByGenreId(long genreId);
}
