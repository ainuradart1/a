package kittyassistant.service;

import kittyassistant.domain.Goal;
import kittyassistant.domain.User;
import kittyassistant.repository.GoalRepository;
import kittyassistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository repo;
    private final UserRepository userRepository;

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found: " + username)));
    }

    public List<Goal> getAll(String username) {
        return repo.findByUserId(getUser(username).getId());
    }

    public Goal create(String title, Integer target, String username) {
        Goal g = new Goal();
        g.setUserId(getUser(username).getId());
        g.setTitle(title);
        g.setTarget(target != null ? target : 100);
        return repo.save(g);
    }

    public Goal updateProgress(Long id, Integer delta, String username) {
        User user = getUser(username);
        Goal g = repo.findById(id).orElseThrow();
        if (!g.getUserId().equals(user.getId()))
            throw new RuntimeException("Access denied");
        g.setCurrent(Math.max(0, Math.min(g.getCurrent() + delta, g.getTarget())));
        return repo.save(g);
    }

    public void delete(Long id, String username) {
        User user = getUser(username);
        Goal g = repo.findById(id).orElseThrow();
        if (!g.getUserId().equals(user.getId()))
            throw new RuntimeException("Access denied");
        repo.deleteById(id);
    }
}