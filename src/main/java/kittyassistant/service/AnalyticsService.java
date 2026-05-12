package kittyassistant.service;

import kittyassistant.domain.DailyProductivitySnapshot;
import kittyassistant.repository.DailySnapshotRepository;
import kittyassistant.repository.TaskRepository;
import kittyassistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final DailySnapshotRepository snapshotRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    public List<DailyProductivitySnapshot> getLast7Days(String email) {
        Long userId = userRepository.findByEmail(email)
                .orElseThrow().getId();
        List<DailyProductivitySnapshot> list =
                snapshotRepository.findTop7ByUserIdOrderByDateDesc(userId);
        Collections.reverse(list);
        return list;
    }

    public Map<String, Object> getWeeklySummary(String email) {
        List<DailyProductivitySnapshot> data = getLast7Days(email);

        int totalFocus  = data.stream().mapToInt(DailyProductivitySnapshot::getFocusMinutes).sum();
        int totalTasks  = data.stream().mapToInt(DailyProductivitySnapshot::getTasksDone).sum();
        double avgMood  = data.stream().mapToDouble(DailyProductivitySnapshot::getAvgMood).average().orElse(0);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalFocusMinutes", totalFocus);
        summary.put("totalTasksDone", totalTasks);
        summary.put("avgMood", Math.round(avgMood * 10.0) / 10.0);
        summary.put("daysTracked", data.size());

        data.stream()
                .max(Comparator.comparingInt(DailyProductivitySnapshot::getFocusMinutes))
                .ifPresent(best -> summary.put("bestDay", best.getDate().toString()));

        return summary;
    }

    public void aggregateToday(String email) {
        Long userId = userRepository.findByEmail(email)
                .orElseThrow().getId();
        LocalDate today = LocalDate.now();

        if (snapshotRepository.existsByUserIdAndDate(userId, today)) return;

        int tasksDone = taskRepository.findAll().stream()
                .filter(t -> t.getUserId() != null && t.getUserId().equals(userId))
                .filter(t -> Boolean.TRUE.equals(t.getCompleted()))
                .mapToInt(t -> 1).sum();

        DailyProductivitySnapshot snap = new DailyProductivitySnapshot();
        snap.setDate(today);
        snap.setUserId(userId);
        snap.setFocusMinutes(0);
        snap.setTasksDone(tasksDone);
        snap.setAvgMood(3.0);
        snap.setHabitsCompleted(0);

        snapshotRepository.save(snap);
    }
}