package com.convopal.demo.dto;

import com.convopal.demo.entity.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotBlank(message = "Receiver username is required")
    private String receiverUsername;
    
    @NotBlank(message = "Message content is required")
    @Size(max = 1000, message = "Message content must be less than 1000 characters")
    private String content;
    
    @NotNull(message = "Message type is required")
    private Message.MessageType type = Message.MessageType.TEXT;
} 