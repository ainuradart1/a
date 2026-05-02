package kittyassistant.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "daily_productivity_snapshot")
@Getter
@Setter
@NoArgsConstructor
public class DailyProductivitySnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private Long userId;

    // Суммарные минуты фокуса за день
    private int focusMinutes;

    // Сколько задач выполнено
    private int tasksDone;

    // Среднее настроение (1-5)
    private double avgMood;

    // Количество выполненных привычек
    private int habitsCompleted;
}