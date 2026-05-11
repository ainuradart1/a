package kittyassistant.service;

import kittyassistant.domain.StudyNote;
import kittyassistant.domain.User;
import kittyassistant.dto.StudyNoteRequest;
import kittyassistant.repository.StudyNoteRepository;
import kittyassistant.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudyNoteService {

    private final StudyNoteRepository repo;
    private final UserRepository userRepo;

    public StudyNoteService(StudyNoteRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    private User getUser(String username) {
        return userRepo.findByUsername(username)
                .orElseGet(() -> userRepo.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username)));
    }

    public List<StudyNote> getAll(String username, Long subjectId) {
        Long uid = getUser(username).getId();
        return subjectId != null
                ? repo.findByUserIdAndSubjectId(uid, subjectId)
                : repo.findByUserId(uid);
    }

    public StudyNote create(StudyNoteRequest req, String username) {
        StudyNote n = new StudyNote();
        n.setUserId(getUser(username).getId());
        n.setSubjectId(req.getSubjectId());
        n.setContent(req.getContent());
        n.setCreatedAt(LocalDateTime.now());
        n.setUpdatedAt(LocalDateTime.now());
        return repo.save(n);
    }

    public void delete(Long id, String username) {
        User user = getUser(username);
        StudyNote n = repo.findById(id).orElseThrow();
        if (!n.getUserId().equals(user.getId())) throw new RuntimeException("Access denied");
        repo.deleteById(id);
    }
}