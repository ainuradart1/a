package kittyassistant.controller;

import kittyassistant.domain.User;
import kittyassistant.dto.LoginRequest;
import kittyassistant.repository.UserRepository;
import kittyassistant.service.AuthService;
import kittyassistant.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
public class AuthController {

    private final AuthService authService;
    private final UserRepository repo;
    private final JwtService jwtService;

    public AuthController(AuthService authService,
                          UserRepository repo,
                          JwtService jwtService) {
        this.authService = authService;
        this.repo = repo;
        this.jwtService = jwtService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<User> register(
            @RequestParam String username,
            @RequestParam String password) {
        User user = authService.register(username, password);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
        User user = repo.findByUsername(req.username)
                .orElseGet(() -> repo.findByEmail(req.username)
                        .orElseThrow());

        if (authService.checkPassword(req.password, user.getPassword())) {
            String token = jwtService.generateToken(user.getUsername());
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(401).body("INVALID_CREDENTIALS");
    }

    // После Google входа — редиректим на html файл
    @GetMapping("/oauth2/redirect")
    public void oauth2Redirect(@RequestParam String token,
                               HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2redirect.html?token=" + token);
    }
}