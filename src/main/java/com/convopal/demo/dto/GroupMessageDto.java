package com.convopal.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessageDto {
    private Long id;
    private Long groupId;
    private UserProfileDto sender;
    private String content;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 