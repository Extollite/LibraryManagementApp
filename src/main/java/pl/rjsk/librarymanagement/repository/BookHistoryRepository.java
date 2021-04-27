package pl.rjsk.librarymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.rjsk.librarymanagement.model.entity.BookHistory;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookHistoryRepository extends JpaRepository<BookHistory, Long> {

    @Query("select distinct bh.bookCopy.id from BookHistory bh " +
            "where bh.bookCopy.id in :bookInstanceIds " +
            "and bh.returnedDate is null")
    List<Long> findAllNotAvailable(Collection<Long> bookInstanceIds);
    
    @Query("select bh.dueDate from BookHistory bh " +
            "where bh.bookCopy.id = :bookInstanceId")
    Optional<OffsetDateTime> findDueDate(Long bookInstanceId);
}
