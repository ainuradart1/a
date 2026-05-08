package kittyassistant.controller;

import kittyassistant.domain.MoodEntry;
import kittyassistant.service.MoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mood")
@RequiredArgsConstructor
public class MoodController {

    private final MoodService service;

    @GetMapping
    public List<MoodEntry> getLast7(@AuthenticationPrincipal UserDetails user) {
        return service.getLast7Days(user.getUsername());
    }

    @PostMapping
    public MoodEntry save(@RequestBody Map<String, Integer> body,
                          @AuthenticationPrincipal UserDetails user) {
        return service.saveToday(body.get("mood"), user.getUsername());
    }
}