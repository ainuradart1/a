package kittyassistant.controller;

import kittyassistant.domain.Flashcard;
import kittyassistant.domain.StudyNote;
import kittyassistant.domain.StudySession;
import kittyassistant.domain.Subject;
import kittyassistant.dto.*;
import kittyassistant.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/study")
public class StudyController {

    private final SubjectService subjectService;
    private final FlashcardService flashcardService;
    private final StudySessionService sessionService;
    private final StudyNoteService noteService;

    public StudyController(SubjectService subjectService,
                           FlashcardService flashcardService,
                           StudySessionService sessionService,
                           StudyNoteService noteService) {
        this.subjectService = subjectService;
        this.flashcardService = flashcardService;
        this.sessionService = sessionService;
        this.noteService = noteService;
    }

    // SUBJECTS

    /** GET /api/study/subjects — список предметов пользователя */
    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getSubjects(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(subjectService.getAll(user.getUsername()));
    }

    //создать предмет
    @PostMapping("/subjects")
    public ResponseEntity<Subject> createSubject(
            @RequestBody SubjectRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(subjectService.create(req, user.getUsername()));
    }

    /** PATCH /api/study/subjects/{id}/progress — обновить прогресс */
    @PatchMapping("/subjects/{id}/progress")
    public ResponseEntity<Subject> updateProgress(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal UserDetails user) {
        int pct = body.getOrDefault("progressPercent", 0);
        return ResponseEntity.ok(subjectService.updateProgress(id, pct, user.getUsername()));
    }

    //удалить предмет
    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<Void> deleteSubject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        subjectService.delete(id, user.getUsername());
        return ResponseEntity.ok().build();
    }

    // FLASHCARDS

    //карточки
    @GetMapping("/flashcards")
    public ResponseEntity<List<Flashcard>> getFlashcards(
            @RequestParam(required = false) Long subjectId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(flashcardService.getAll(user.getUsername(), subjectId));
    }

    // создать карточку
    @PostMapping("/flashcards")
    public ResponseEntity<Flashcard> createFlashcard(
            @RequestBody FlashcardRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(flashcardService.create(req, user.getUsername()));
    }

    //отметить сложность
    @PostMapping("/flashcards/{id}/review")
    public ResponseEntity<Flashcard> reviewFlashcard(
            @PathVariable Long id,
            @RequestBody FlashcardReviewRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(flashcardService.review(id, req, user.getUsername()));
    }

    // STUDY SESSIONS

    /** POST /api/study/sessions/start — начать сессию */
    @PostMapping("/sessions/start")
    public ResponseEntity<StudySession> startSession(
            @RequestBody StudySessionRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(sessionService.start(req, user.getUsername()));
    }

    //завершить сессию
    @PostMapping("/sessions/{id}/end")
    public ResponseEntity<StudySession> endSession(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(sessionService.end(id, user.getUsername()));
    }

    //сессии за сегодня
    @GetMapping("/sessions/today")
    public ResponseEntity<List<StudySession>> todaySessions(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(sessionService.getToday(user.getUsername()));
    }

    // STUDY NOTES

    //заметки
    @GetMapping("/notes")
    public ResponseEntity<List<StudyNote>> getNotes(
            @RequestParam(required = false) Long subjectId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(noteService.getAll(user.getUsername(), subjectId));
    }

    //создать заметку
    @PostMapping("/notes")
    public ResponseEntity<StudyNote> createNote(
            @RequestBody StudyNoteRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(noteService.create(req, user.getUsername()));
    }

    //удалить заметку
    @DeleteMapping("/notes/{id}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        noteService.delete(id, user.getUsername());
        return ResponseEntity.ok().build();
    }
}