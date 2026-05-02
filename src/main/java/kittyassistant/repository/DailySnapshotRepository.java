package kittyassistant.repository;

import kittyassistant.domain.DailyProductivitySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailySnapshotRepository
        extends JpaRepository<DailyProductivitySnapshot, Long> {

    List<DailyProductivitySnapshot> findTop7ByUserIdOrderByDateDesc(Long userId);

    boolean existsByUserIdAndDate(Long userId, LocalDate date);
}