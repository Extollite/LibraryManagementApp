package pl.rjsk.librarymanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.repository.UserRepository;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final long USER_ID = 1L;
    private static final String PESEL = "pesel";
    private static final String PASSWORD = "password";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void delete_thrownException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unable to delete user with given id: " + USER_ID);

        verify(userRepository).findById(eq(USER_ID));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void delete() {
        var user = new User();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.delete(USER_ID);

        verify(userRepository).findById(eq(USER_ID));
        verify(userRepository).delete(eq(user));
        verifyNoInteractions(passwordEncoder);
    }

    private static Stream<String> saveEmptyPasswordProvider() {
        return Stream.of(null, "");
    }

    @ParameterizedTest
    @MethodSource("saveEmptyPasswordProvider")
    void save_emptyPassword(String password) {
        var user = new User();
        user.setPassword(password);

        assertThatThrownBy(() -> userService.save(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password must contain at least 5 characters");

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void save_peselExists() {
        var user = new User();
        user.setPesel(PESEL);
        user.setPassword(PASSWORD);

        when(userRepository.findByPesel(anyString())).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.save(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with pesel " + PESEL + " already exists");

        verify(userRepository).findByPesel(eq(PESEL));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void save() {
        var user = new User();
        user.setPesel(PESEL);
        user.setPassword(PASSWORD);

        when(userRepository.findByPesel(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(USER_ID);
            return savedUser;
        });

        User result = userService.save(user);

        assertThat(result)
                .isNotNull()
                .extracting("id", "pesel", "password")
                .containsExactly(USER_ID, PESEL, PASSWORD);

        verify(userRepository).findByPesel(eq(PESEL));
        verify(passwordEncoder).encode(eq(PASSWORD));
        verify(userRepository).save(eq(user));
    }

    @ParameterizedTest
    @MethodSource("saveEmptyPasswordProvider")
    void updatePassword_emptyPassword(String password) {

        assertThatThrownBy(() -> userService.updatePassword(PESEL, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password must contain at least 5 characters");

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void updatePassword_peselExists() {
        when(userRepository.findByPesel(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updatePassword(PESEL, PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unable to fetch user with given pesel: " + PESEL);

        verify(userRepository).findByPesel(eq(PESEL));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updatePassword() {
        var user = new User();
        user.setPesel(PESEL);
        user.setPassword("pass");

        when(userRepository.findByPesel(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updatePassword(PESEL, PASSWORD);

        assertThat(result)
                .isNotNull()
                .extracting("pesel", "password")
                .containsExactly(PESEL, PASSWORD);

        verify(userRepository).findByPesel(eq(PESEL));
        verify(passwordEncoder).encode(eq(PASSWORD));
    }
}