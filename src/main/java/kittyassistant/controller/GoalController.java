package kittyassistant.controller;

import kittyassistant.domain.Goal;
import kittyassistant.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService service;

    @GetMapping
    public List<Goal> getAll(@AuthenticationPrincipal UserDetails user) {
        return service.getAll(user.getUsername());
    }

    @PostMapping
    public Goal create(@RequestBody Map<String, Object> body,
                       @AuthenticationPrincipal UserDetails user) {
        String title = (String) body.get("title");
        Integer target = body.get("target") != null
                ? ((Number) body.get("target")).intValue() : 100;
        return service.create(title, target, user.getUsername());
    }

    @PatchMapping("/{id}/progress")
    public Goal updateProgress(@PathVariable Long id,
                               @RequestBody Map<String, Integer> body,
                               @AuthenticationPrincipal UserDetails user) {
        return service.updateProgress(id, body.get("delta"), user.getUsername());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails user) {
        service.delete(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}