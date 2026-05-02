package kittyassistant.service;

import kittyassistant.domain.AuthProvider;
import kittyassistant.domain.User;
import kittyassistant.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    // PasswordEncoder вместо BCryptPasswordEncoder напрямую
    public AuthService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public User register(String username, String password) {
        User u = new User();
        u.setUsername(username);
        // email = username если не передан отдельно
        u.setEmail(username);
        u.setPassword(encoder.encode(password));
        u.setRole("USER");
        u.setProvider(AuthProvider.LOCAL);
        return repo.save(u);
    }

    public boolean checkPassword(String raw, String encoded) {
        return encoder.matches(raw, encoded);
    }
}