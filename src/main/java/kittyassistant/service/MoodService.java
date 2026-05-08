package kittyassistant.service;

import kittyassistant.domain.MoodEntry;
import kittyassistant.domain.User;
import kittyassistant.repository.MoodRepository;
import kittyassistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MoodService {

    private final MoodRepository repo;
    private final UserRepository userRepository;

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found: " + username)));
    }

    // Сохранить или обновить настроение за сегодня
    public MoodEntry saveToday(Integer mood, String username) {
        User user = getUser(username);
        LocalDate today = LocalDate.now();

        MoodEntry entry = repo.findByUserIdAndDate(user.getId(), today)
                .orElseGet(() -> {
                    MoodEntry e = new MoodEntry();
                    e.setUserId(user.getId());
                    e.setDate(today);
                    return e;
                });

        entry.setMood(mood);
        return repo.save(entry);
    }

    // Последние 7 дней
    public List<MoodEntry> getLast7Days(String username) {
        return repo.findByUserIdOrderByDateDesc(getUser(username).getId())
                .stream().limit(7).toList();
    }
}