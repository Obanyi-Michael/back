package com.convopal.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactRequestDto {
    @NotBlank(message = "Username is required")
    private String username;
} 