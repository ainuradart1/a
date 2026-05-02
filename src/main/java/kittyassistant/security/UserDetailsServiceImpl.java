package kittyassistant.security;

import kittyassistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        kittyassistant.domain.User user = userRepository
                .findByUsername(username)
                .orElseGet(() -> userRepository
                        .findByEmail(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "User not found: " + username)));

        return User.builder()
                .username(user.getUsername() != null
                        ? user.getUsername()
                        : user.getEmail())
                .password(user.getPassword() != null
                        ? user.getPassword()
                        : "")
                .roles(user.getRole())
                .build();
    }
}