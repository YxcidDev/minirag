package com.tecnologico.minirag.controller;

import com.tecnologico.minirag.model.QueryLog;
import com.tecnologico.minirag.repository.QueryLogRepository;
import com.tecnologico.minirag.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;
    private final QueryLogRepository queryLogRepository;

    public ChatController(
            ChatService chatService,
            QueryLogRepository queryLogRepository) {
        this.chatService = chatService;
        this.queryLogRepository = queryLogRepository;
    }

    @PostMapping("/chat")
    public Map<String, String> ask(@RequestBody Map<String, String> body) {
        String question = body.get("question");

        if (question == null || question.isBlank()) {
            return Map.of("answer", "You must enter a question.");
        }

        String answer = chatService.ask(question);

        return Map.of("answer", answer);
    }

    @GetMapping("/history")
    public List<QueryLog> history() {
        return queryLogRepository.findAll();
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "OK",
                "application", "MiniRAG"
        );
    }
}