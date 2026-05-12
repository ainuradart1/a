package kittyassistant.service;

import kittyassistant.domain.Task;
import kittyassistant.domain.User;
import kittyassistant.repository.TaskRepository;
import kittyassistant.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repo;
    private final UserRepository userRepository;

    public TaskService(TaskRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found: " + username)));
    }

    public Task create(Task t, String username) {
        User user = getUser(username);
        t.setUserId(user.getId());
        t.setCompleted(false);
        return repo.save(t);
    }

    public List<Task> getAll(String username) {
        User user = getUser(username);
        return repo.findByUserId(user.getId());
    }

    public Task complete(Long id, String username) {
        User user = getUser(username);
        Task t = repo.findById(id).orElseThrow();

        if (!t.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        t.setCompleted(true);
        return repo.save(t);
    }

    public void delete(Long id, String username) {
        User user = getUser(username);
        Task t = repo.findById(id).orElseThrow();

        if (!t.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        repo.deleteById(id);
    }
}