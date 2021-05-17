package pl.rjsk.librarymanagement.security.data;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.rjsk.librarymanagement.model.entity.User;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
@ToString
@EqualsAndHashCode
public class LibraryUserDetails implements UserDetails {

    private final User libraryUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return libraryUser.getRoles()
                .stream()
                .map(name -> "ROLE_" + name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return libraryUser.getPassword();
    }

    @Override
    public String getUsername() {
        return libraryUser.getPesel();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
