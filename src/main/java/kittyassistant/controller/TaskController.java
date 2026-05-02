package kittyassistant.controller;

import kittyassistant.domain.Task;
import kittyassistant.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // Создать задачу
    @PostMapping
    public ResponseEntity<Task> create(
            @RequestBody Task t,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                service.create(t, userDetails.getUsername()));
    }

    // Получить все задачи текущего пользователя
    @GetMapping
    public ResponseEntity<List<Task>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                service.getAll(userDetails.getUsername()));
    }

    // Отметить как выполненную
    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> complete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                service.complete(id, userDetails.getUsername()));
    }

    // Удалить задачу
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        service.delete(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}