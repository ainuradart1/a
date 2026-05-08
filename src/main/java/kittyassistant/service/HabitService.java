package kittyassistant.service;

import kittyassistant.domain.Habit;
import kittyassistant.domain.User;
import kittyassistant.repository.HabitRepository;
import kittyassistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository repo;
    private final UserRepository userRepository;

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found: " + username)));
    }

    public List<Habit> getAll(String username) {
        return repo.findByUserId(getUser(username).getId());
    }

    public Habit create(String title, String username) {
        Habit h = new Habit();
        h.setUserId(getUser(username).getId());
        h.setTitle(title);
        return repo.save(h);
    }

    public Habit toggle(Long id, String username) {
        User user = getUser(username);
        Habit h = repo.findById(id).orElseThrow();
        if (!h.getUserId().equals(user.getId()))
            throw new RuntimeException("Access denied");
        h.setCompletedToday(!h.getCompletedToday());
        return repo.save(h);
    }

    public void delete(Long id, String username) {
        User user = getUser(username);
        Habit h = repo.findById(id).orElseThrow();
        if (!h.getUserId().equals(user.getId()))
            throw new RuntimeException("Access denied");
        repo.deleteById(id);
    }
}