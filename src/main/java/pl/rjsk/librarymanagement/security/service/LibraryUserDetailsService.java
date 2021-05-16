package pl.rjsk.librarymanagement.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.repository.UserRepository;
import pl.rjsk.librarymanagement.security.data.LibraryUserDetails;

@Service
@RequiredArgsConstructor
@Slf4j
public class LibraryUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByPesel(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return LibraryUserDetails.of(user);
    }
}
