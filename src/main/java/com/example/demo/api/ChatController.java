package com.example.demo.api;

import com.example.demo.dto.ChatRequestDto;
import com.example.demo.dto.ChatResponseDto;
import com.example.demo.service.ClaudeChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ClaudeChatService chatService;

    public ChatController(ClaudeChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponseDto chat(@RequestBody ChatRequestDto request) {
        return new ChatResponseDto(chatService.chat(request.messages()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ChatResponseDto> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ChatResponseDto(e.getMessage()));
    }

    @ExceptionHandler(ClaudeChatService.ClaudeException.class)
    public ResponseEntity<ChatResponseDto> claudeError(ClaudeChatService.ClaudeException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ChatResponseDto(e.getMessage()));
    }
}
