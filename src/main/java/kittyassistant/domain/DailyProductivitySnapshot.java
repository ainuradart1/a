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
    private int focusMinutes;
    private int tasksDone;
    private double avgMood;
    private int habitsCompleted;
}