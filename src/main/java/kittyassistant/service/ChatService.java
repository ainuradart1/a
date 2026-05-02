package kittyassistant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kittyassistant.domain.ChatMessage;
import kittyassistant.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${groq.api.key:}")
    private String apiKey;

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public ChatMessage saveMessage(String sender, String message) {
        String response = generateResponse(message);

        ChatMessage chat = new ChatMessage();
        chat.setSender(sender);
        chat.setMessage(message);
        chat.setResponse(response);
        chat.setTimestamp(LocalDateTime.now());

        return chatRepository.save(chat);
    }

    private String generateResponse(String message) {
        if (apiKey == null || apiKey.isBlank()) {
            return "API ключ не настроен. Добавь groq.api.key в application.properties";
        }
        return callGroqAPI(message);
    }

    private String callGroqAPI(String userMessage) {
        try {
            String systemPrompt = "Ты дружелюбный персональный ассистент по имени Kitty. " +
                    "Отвечай кратко (1-3 предложения), всегда на русском языке, " +
                    "будь полезной и позитивной. Используй эмодзи умеренно. " +
                    "Не представляйся каждый раз — просто помогай.";

            ObjectMapper mapper = new ObjectMapper();
            var root = mapper.createObjectNode();
            root.put("model", "llama-3.1-8b-instant");
            root.put("max_tokens", 400);
            root.put("temperature", 0.7);

            var messagesArr = mapper.createArrayNode();

            var systemNode = mapper.createObjectNode();
            systemNode.put("role", "system");
            systemNode.put("content", systemPrompt);
            messagesArr.add(systemNode);

            var userNode = mapper.createObjectNode();
            userNode.put("role", "user");
            userNode.put("content", userMessage);
            messagesArr.add(userNode);

            root.set("messages", messagesArr);

            String body = mapper.writeValueAsString(root);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GROQ_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return parseGroqResponse(response.body());

        } catch (Exception e) {
            return "Произошла ошибка при обращении к ИИ: " + e.getMessage();
        }
    }

    private String parseGroqResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);

            if (root.has("error")) {
                String msg = root.path("error").path("message").asText("Неизвестная ошибка");
                return "Ошибка API: " + msg;
            }

            // Groq использует OpenAI формат: choices[0].message.content
            return root
                    .path("choices").get(0)
                    .path("message")
                    .path("content")
                    .asText("Нет ответа.");

        } catch (Exception e) {
            return "Не удалось разобрать ответ от ИИ: " + e.getMessage();
        }
    }

    public List<ChatMessage> getAllMessages() {
        return chatRepository.findAll();
    }

    public List<ChatMessage> getMessagesBySender(String sender) {
        return chatRepository.findBySender(sender);
    }
}
//http://localhost:8080/