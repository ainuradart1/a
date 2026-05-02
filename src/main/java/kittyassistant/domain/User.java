package kittyassistant.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String username;

    // Пароль — nullable, потому что Google-пользователи без пароля
    private String password;

    // Ссылка на аватар (берётся из Google)
    private String picture;

    // LOCAL = обычный вход, GOOGLE = через Google
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL;

    // ID пользователя в Google (sub)
    private String providerId;

    @Column(nullable = false)
    private String role = "USER";
}