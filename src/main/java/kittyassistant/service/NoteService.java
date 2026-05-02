package kittyassistant.service;

import kittyassistant.domain.Note;
import kittyassistant.domain.User;
import kittyassistant.repository.NoteRepository;
import kittyassistant.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository repo;
    private final UserRepository userRepository;

    public NoteService(NoteRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    // Найти пользователя по username или email
    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found: " + username)));
    }

    // Создать заметку — автор берётся из токена, не из тела запроса
    public Note create(String username, String title, String content) {
        User user = getUser(username);

        Note note = new Note();
        note.setAuthor(user.getUsername() != null
                ? user.getUsername()
                : user.getEmail());
        note.setUserId(user.getId());
        note.setTitle(title);
        note.setContent(content);
        note.setCreatedAt(LocalDateTime.now());
        return repo.save(note);
    }

    // Только заметки текущего пользователя
    public List<Note> getByUser(String username) {
        User user = getUser(username);
        return repo.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    // Удалить — только свою заметку
    public void delete(Long id, String username) {
        User user = getUser(username);
        Note note = repo.findById(id).orElseThrow();

        if (!note.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        repo.deleteById(id);
    }
}