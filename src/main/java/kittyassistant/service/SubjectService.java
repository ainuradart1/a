package kittyassistant.service;

import kittyassistant.domain.Subject;
import kittyassistant.domain.User;
import kittyassistant.dto.SubjectRequest;
import kittyassistant.repository.SubjectRepository;
import kittyassistant.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository repo;
    private final UserRepository userRepo;

    public SubjectService(SubjectRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    private User getUser(String username) {
        return userRepo.findByUsername(username)
                .orElseGet(() -> userRepo.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username)));
    }

    public List<Subject> getAll(String username) {
        return repo.findByUserId(getUser(username).getId());
    }

    public Subject create(SubjectRequest req, String username) {
        Subject s = new Subject();
        s.setUserId(getUser(username).getId());
        s.setName(req.getName());
        s.setEmoji(req.getEmoji());
        s.setColor(req.getColor());
        s.setProgressPercent(req.getProgressPercent() != null ? req.getProgressPercent() : 0);
        return repo.save(s);
    }

    public Subject updateProgress(Long id, int progressPercent, String username) {
        User user = getUser(username);
        Subject s = repo.findById(id).orElseThrow();
        if (!s.getUserId().equals(user.getId())) throw new RuntimeException("Access denied");
        s.setProgressPercent(progressPercent);
        return repo.save(s);
    }

    public void delete(Long id, String username) {
        User user = getUser(username);
        Subject s = repo.findById(id).orElseThrow();
        if (!s.getUserId().equals(user.getId())) throw new RuntimeException("Access denied");
        repo.deleteById(id);
    }
}