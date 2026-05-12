package kittyassistant.service;

import kittyassistant.domain.Flashcard;
import kittyassistant.domain.User;
import kittyassistant.dto.FlashcardRequest;
import kittyassistant.dto.FlashcardReviewRequest;
import kittyassistant.repository.FlashcardRepository;
import kittyassistant.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FlashcardService {

    private final FlashcardRepository repo;
    private final UserRepository userRepo;

    public FlashcardService(FlashcardRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    private User getUser(String username) {
        return userRepo.findByUsername(username)
                .orElseGet(() -> userRepo.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username)));
    }

    public List<Flashcard> getAll(String username, Long subjectId) {
        Long uid = getUser(username).getId();
        return subjectId != null
                ? repo.findByUserIdAndSubjectId(uid, subjectId)
                : repo.findByUserId(uid);
    }

    public Flashcard create(FlashcardRequest req, String username) {
        Flashcard f = new Flashcard();
        f.setUserId(getUser(username).getId());
        f.setSubjectId(req.getSubjectId());
        f.setFrontText(req.getFrontText());
        f.setBackText(req.getBackText());
        f.setNextReview(LocalDate.now());
        return repo.save(f);
    }
    public Flashcard review(Long id, FlashcardReviewRequest req, String username) {
        User user = getUser(username);
        Flashcard f = repo.findById(id).orElseThrow();
        if (!f.getUserId().equals(user.getId())) throw new RuntimeException("Access denied");

        f.setDifficulty(req.getDifficulty());
        f.setNextReview(switch (req.getDifficulty()) {
            case EASY   -> LocalDate.now().plusDays(3);
            case MEDIUM -> LocalDate.now().plusDays(1);
            case HARD   -> LocalDate.now();
        });
        return repo.save(f);
    }
}