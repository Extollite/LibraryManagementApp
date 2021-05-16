package pl.rjsk.librarymanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.security.data.UserRole;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByRolesIsNotContaining(Pageable pageable, UserRole role);

    Optional<User> findByPesel(String pesel);
}
