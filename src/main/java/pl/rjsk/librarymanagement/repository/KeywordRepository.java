package pl.rjsk.librarymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rjsk.librarymanagement.model.entity.Keyword;

import java.util.Collection;
import java.util.Set;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Set<Keyword> findAllByNameIn(Collection<String> names);
}
