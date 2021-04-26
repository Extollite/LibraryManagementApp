package pl.rjsk.librarymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.rjsk.librarymanagement.model.entity.BookHistory;

import java.util.Collection;
import java.util.List;

@Repository
public interface BookHistoryRepository extends JpaRepository<BookHistory, Long> {

    @Query("select distinct bh.bookInstance.id from BookHistory bh " +
            "where bh.bookInstance.id in :bookInstanceIds " +
            "and bh.returnedDate is null")
    List<Long> findAllNotAvailable(Collection<Long> bookInstanceIds);
}
