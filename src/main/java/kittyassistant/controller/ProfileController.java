package kittyassistant.controller;

import kittyassistant.domain.User;
import kittyassistant.dto.ProfileUpdateRequest;
import kittyassistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;

    // Получить профиль текущего пользователя
    @GetMapping
    public ResponseEntity<Map<String, Object>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseGet(() -> userRepository
                        .findByEmail(userDetails.getUsername())
                        .orElseThrow());

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("id",       user.getId());
        profile.put("username", user.getUsername());
        profile.put("email",    user.getEmail());
        profile.put("picture",  user.getPicture());
        profile.put("bio",      user.getBio());
        profile.put("provider", user.getProvider().name());

        return ResponseEntity.ok(profile);
    }

    // Обновить профиль
    @PutMapping
    public ResponseEntity<Map<String, Object>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfileUpdateRequest req) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseGet(() -> userRepository
                        .findByEmail(userDetails.getUsername())
                        .orElseThrow());

        if (req.getUsername() != null && !req.getUsername().isBlank()) {
            user.setUsername(req.getUsername());
        }
        if (req.getBio() != null) {
            user.setBio(req.getBio());
        }
        if (req.getPicture() != null && !req.getPicture().isBlank()) {
            user.setPicture(req.getPicture());
        }

        userRepository.save(user);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("username", user.getUsername());
        response.put("email",    user.getEmail());
        response.put("picture",  user.getPicture());
        response.put("bio",      user.getBio());

        return ResponseEntity.ok(response);
    }
}