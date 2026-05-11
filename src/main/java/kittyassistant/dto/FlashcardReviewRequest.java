package kittyassistant.dto;

import kittyassistant.domain.Flashcard;

public class FlashcardReviewRequest {
    private Long flashcardId;
    private Flashcard.Difficulty difficulty; // EASY, MEDIUM, HARD

    public Long getFlashcardId() { return flashcardId; }
    public void setFlashcardId(Long flashcardId) { this.flashcardId = flashcardId; }

    public Flashcard.Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Flashcard.Difficulty difficulty) { this.difficulty = difficulty; }
}