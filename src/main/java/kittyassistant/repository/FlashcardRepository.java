package kittyassistant.repository;

import kittyassistant.domain.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    List<Flashcard> findByUserId(Long userId);
    List<Flashcard> findByUserIdAndSubjectId(Long userId, Long subjectId);
}