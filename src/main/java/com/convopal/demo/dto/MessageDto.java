package com.convopal.demo.dto;

import com.convopal.demo.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private UserProfileDto sender;
    private UserProfileDto receiver;
    private String content;
    private Message.MessageType type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static MessageDto fromMessage(Message message, UserProfileDto senderProfile, UserProfileDto receiverProfile) {
        return new MessageDto(
            message.getId(),
            senderProfile,
            receiverProfile,
            message.getContent(),
            message.getType(),
            message.getIsRead(),
            message.getCreatedAt(),
            message.getUpdatedAt()
        );
    }
} 