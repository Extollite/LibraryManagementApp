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
            "where bh.bookCopy.id in :bookCopyIds " +
            "and bh.returnedDate is null")
    List<Long> findAllNotAvailable(Collection<Long> bookCopyIds);

    @Query("select bh.dueDate from BookHistory bh " +
            "where bh.bookCopy.id = :bookCopyId " +
            "and bh.returnedDate is null")
    Optional<OffsetDateTime> findDueDate(long bookCopyId);

    @Query("select bh from BookHistory bh " +
            "where bh.bookCopy.id = :bookCopyId " +
            "and bh.returnedDate is null")
    Optional<BookHistory> findNotReturnedByBookId(long bookCopyId);

    void deleteAllByBookCopyId(long bookCopyId);
}
