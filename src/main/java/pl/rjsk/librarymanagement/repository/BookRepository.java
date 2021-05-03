package pl.rjsk.librarymanagement.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pl.rjsk.librarymanagement.model.entity.Book;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
    
}
