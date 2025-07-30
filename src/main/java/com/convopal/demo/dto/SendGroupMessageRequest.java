package com.convopal.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendGroupMessageRequest {
    
    @NotNull(message = "Group ID is required")
    private Long groupId;
    
    @NotBlank(message = "Message content is required")
    private String content;
    
    @NotBlank(message = "Message type is required")
    private String type;
} 