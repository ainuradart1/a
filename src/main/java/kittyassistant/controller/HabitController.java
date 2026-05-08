package kittyassistant.controller;

import kittyassistant.domain.Habit;
import kittyassistant.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService service;

    @GetMapping
    public List<Habit> getAll(@AuthenticationPrincipal UserDetails user) {
        return service.getAll(user.getUsername());
    }

    @PostMapping
    public Habit create(@RequestBody Map<String, String> body,
                        @AuthenticationPrincipal UserDetails user) {
        return service.create(body.get("title"), user.getUsername());
    }

    @PatchMapping("/{id}/toggle")
    public Habit toggle(@PathVariable Long id,
                        @AuthenticationPrincipal UserDetails user) {
        return service.toggle(id, user.getUsername());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails user) {
        service.delete(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}