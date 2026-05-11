package kittyassistant.repository;

import kittyassistant.domain.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    List<StudySession> findByUserId(Long userId);
    List<StudySession> findByUserIdAndStartedAtBetween(Long userId,
                                                       LocalDateTime from,
                                                       LocalDateTime to);
}