package kittyassistant.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String response;

    private LocalDateTime timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String sender, String message, String response, LocalDateTime timestamp) {
        this.sender = sender;
        this.message = message;
        this.response = response;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getResponse() {
        return response;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}