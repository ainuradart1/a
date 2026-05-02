package kittyassistant.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kittyassistant.domain.User;
import kittyassistant.repository.UserRepository;
import kittyassistant.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + email));

        // Генерируем JWT — передаём username
        // Если username null (новый Google пользователь) — используем email
        String subject = user.getUsername() != null
                ? user.getUsername()
                : user.getEmail();

        String token = jwtService.generateToken(subject);

        getRedirectStrategy().sendRedirect(
                request, response,
                "/oauth2/redirect?token=" + token);
    }
}