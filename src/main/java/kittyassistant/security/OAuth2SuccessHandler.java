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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
        String email   = oAuth2User.getAttribute("email");
        String name    = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        String subject = user.getUsername() != null
                ? user.getUsername()
                : user.getEmail();

        String token = jwtService.generateToken(subject);

        // Передаём и токен и имя в URL
        String encodedName = URLEncoder.encode(
                name != null ? name : subject,
                StandardCharsets.UTF_8);

        getRedirectStrategy().sendRedirect(
                request, response,
                "/oauth2redirect.html?token=" + token + "&name=" + encodedName);
    }
}