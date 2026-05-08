package kittyassistant.repository;

import kittyassistant.domain.MoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MoodRepository extends JpaRepository<MoodEntry, Long> {
    List<MoodEntry> findByUserIdOrderByDateDesc(Long userId);
    Optional<MoodEntry> findByUserIdAndDate(Long userId, LocalDate date);
}