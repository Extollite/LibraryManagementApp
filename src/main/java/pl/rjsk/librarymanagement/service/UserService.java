package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.repository.UserRepository;
import pl.rjsk.librarymanagement.security.data.UserRole;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAllByRolesIsNotContaining(pageable, UserRole.ADMIN);
    }

    @Transactional
    public User getUserByPesel(String pesel) {
        return userRepository.findByPesel(pesel)
                .orElseThrow(() -> new IllegalArgumentException("Unable to fetch user with given pesel: " + pesel));
    }

    @Transactional
    public void delete(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unable to delete user with given id: " + userId));

        userRepository.delete(user);
    }

    @Transactional
    public User save(User user) {
        if (!StringUtils.hasLength(user.getPassword())) {
            throw new IllegalArgumentException("Password cannot be empty!");
        }
        userRepository.findByPesel(user.getPesel())
                .ifPresent((dbUser) -> {
                    throw new IllegalArgumentException("User with pesel " + user.getPesel() + " already exists");
                });

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(UserRole.USER);

        return userRepository.save(user);
    }

    @Transactional
    public User updatePassword(String pesel, String password) {
        if (!StringUtils.hasLength(password)) {
            throw new IllegalArgumentException("Password cannot be empty!");
        }
        User user = userRepository.findByPesel(pesel)
                .orElseThrow(() -> new IllegalArgumentException("Unable to fetch user with given pesel: " + pesel));

        user.setPassword(passwordEncoder.encode(password));

        return user;
    }
}
