package pl.rjsk.librarymanagement.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.repository.UserRepository;
import pl.rjsk.librarymanagement.security.data.UserRole;
import pl.rjsk.librarymanagement.service.RandomPasswordGeneratorService;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultUsersSetup implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RandomPasswordGeneratorService randomPasswordGeneratorService;

    @Override
    @Transactional
    public void run(String... args) {
        var adminOptional = userRepository.findByPesel("admin");
        if (adminOptional.isEmpty()) {
            User user = new User();
            user.setPesel("admin");
            user.setFirstName("Admin");
            user.setLastName("Admin");
            String testPass = randomPasswordGeneratorService.generateSecureRandomPassword();
            log.info("Temp admin passwd: " + testPass);
            user.setPassword(passwordEncoder.encode("admin"));
            user.getRoles().add(UserRole.ADMIN);
            userRepository.save(user);
        }

        var userOptional = userRepository.findByPesel("user");
        if (userOptional.isEmpty()) {
            User user = new User();
            user.setPesel("user");
            user.setFirstName("User");
            user.setLastName("User");
            String testPass = randomPasswordGeneratorService.generateSecureRandomPassword();
            log.info("Temp user passwd: " + testPass);
            user.setPassword(passwordEncoder.encode("user1"));
            user.getRoles().add(UserRole.USER);
            userRepository.save(user);
        }
    }
}
