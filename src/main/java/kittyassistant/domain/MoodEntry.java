package kittyassistant.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "mood_entries")
@Getter @Setter @NoArgsConstructor
public class MoodEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    // 1=очень плохо, 2=плохо, 3=нейтрально, 4=хорошо, 5=отлично
    private Integer mood;

    private LocalDate date;
}