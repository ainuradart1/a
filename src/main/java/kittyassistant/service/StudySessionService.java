package kittyassistant.service;

import kittyassistant.domain.StudySession;
import kittyassistant.domain.User;
import kittyassistant.dto.StudySessionRequest;
import kittyassistant.repository.StudySessionRepository;
import kittyassistant.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudySessionService {

    private final StudySessionRepository repo;
    private final UserRepository userRepo;

    public StudySessionService(StudySessionRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    private User getUser(String username) {
        return userRepo.findByUsername(username)
                .orElseGet(() -> userRepo.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username)));
    }

    public StudySession start(StudySessionRequest req, String username) {
        StudySession s = new StudySession();
        s.setUserId(getUser(username).getId());
        s.setGoalText(req.getGoalText());
        s.setSubjectId(req.getSubjectId());
        s.setStartedAt(LocalDateTime.now());
        s.setCompleted(false);
        return repo.save(s);
    }

    public StudySession end(Long id, String username) {
        User user = getUser(username);
        StudySession s = repo.findById(id).orElseThrow();
        if (!s.getUserId().equals(user.getId())) throw new RuntimeException("Access denied");
        s.setEndedAt(LocalDateTime.now());
        s.setCompleted(true);
        return repo.save(s);
    }

    /** Сессии за сегодня (для статистики на фронте) */
    public List<StudySession> getToday(String username) {
        Long uid = getUser(username).getId();
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to = from.plusDays(1);
        return repo.findByUserIdAndStartedAtBetween(uid, from, to);
    }
}