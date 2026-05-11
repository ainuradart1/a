package kittyassistant.repository;

import kittyassistant.domain.StudyNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudyNoteRepository extends JpaRepository<StudyNote, Long> {
    List<StudyNote> findByUserId(Long userId);
    List<StudyNote> findByUserIdAndSubjectId(Long userId, Long subjectId);
}