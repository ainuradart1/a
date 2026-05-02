package kittyassistant.controller;

import kittyassistant.domain.DailyProductivitySnapshot;
import kittyassistant.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // Данные за 7 дней для Chart.js
    @GetMapping("/weekly")
    public ResponseEntity<List<DailyProductivitySnapshot>> getWeekly(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                analyticsService.getLast7Days(userDetails.getUsername()));
    }

    // Сводка: итого за неделю
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                analyticsService.getWeeklySummary(userDetails.getUsername()));
    }

    // Ручной запуск агрегации (для тестов)
    @PostMapping("/aggregate")
    public ResponseEntity<String> runAggregation(
            @AuthenticationPrincipal UserDetails userDetails) {
        analyticsService.aggregateToday(userDetails.getUsername());
        return ResponseEntity.ok("Агрегация выполнена успешно");
    }
}