package kittyassistant.controller;

import kittyassistant.domain.ChatMessage;
import kittyassistant.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ChatMessage sendMessage(
            @RequestParam String sender,
            @RequestParam String message
    ) {
        return chatService.saveMessage(sender, message);
    }

    @GetMapping("/all")
    public List<ChatMessage> getAll() {
        return chatService.getAllMessages();
    }

    @GetMapping("/user/{sender}")
    public List<ChatMessage> getBySender(@PathVariable String sender) {
        return chatService.getMessagesBySender(sender);
    }
}