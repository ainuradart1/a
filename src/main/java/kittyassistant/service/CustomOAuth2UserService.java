package kittyassistant.service;

import kittyassistant.domain.AuthProvider;
import kittyassistant.domain.User;
import kittyassistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email   = (String) attributes.get("email");
        String name    = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        String sub     = (String) attributes.get("sub");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name);
            newUser.setProvider(AuthProvider.GOOGLE);
            newUser.setProviderId(sub);
            newUser.setRole("USER");
            return newUser;
        });
        user.setUsername(name);
        user.setPicture(picture);
        userRepository.save(user);

        return oAuth2User;
    }
}