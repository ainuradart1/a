package kittyassistant.controller;

import kittyassistant.domain.Note;
import kittyassistant.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    // Создать заметку
    @PostMapping
    public ResponseEntity<Note> create(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        Note note = service.create(
                userDetails.getUsername(),
                body.getOrDefault("title", "Без названия"),
                body.getOrDefault("content", "")
        );
        return ResponseEntity.ok(note);
    }

    // Получить все заметки текущего пользователя
    @GetMapping
    public ResponseEntity<List<Note>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                service.getByUser(userDetails.getUsername()));
    }

    // Удалить заметку
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        service.delete(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}