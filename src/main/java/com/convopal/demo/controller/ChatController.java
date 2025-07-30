package com.convopal.demo.controller;

import com.convopal.demo.dto.ApiResponse;
import com.convopal.demo.dto.MessageDto;
import com.convopal.demo.dto.SendMessageRequest;
import com.convopal.demo.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {
    
    private final ChatService chatService;
    
    @PostMapping("/messages")
    public ResponseEntity<ApiResponse<MessageDto>> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        try {
            MessageDto message = chatService.sendMessage(request);
            return ResponseEntity.ok(ApiResponse.success("Message sent successfully", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/conversation/{username}")
    public ResponseEntity<ApiResponse<List<MessageDto>>> getConversation(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            List<MessageDto> messages = chatService.getConversation(username, page, size);
            return ResponseEntity.ok(ApiResponse.success("Conversation retrieved successfully", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/messages/read/{username}")
    public ResponseEntity<ApiResponse<String>> markMessagesAsRead(@PathVariable String username) {
        try {
            chatService.markMessagesAsRead(username);
            return ResponseEntity.ok(ApiResponse.success("Messages marked as read", "Messages updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/unread-count/{username}")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable String username) {
        try {
            Long count = chatService.getUnreadCount(username);
            return ResponseEntity.ok(ApiResponse.success("Unread count retrieved", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<MessageDto>>> getUnreadMessages() {
        try {
            List<MessageDto> messages = chatService.getUnreadMessages();
            return ResponseEntity.ok(ApiResponse.success("Unread messages retrieved", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 