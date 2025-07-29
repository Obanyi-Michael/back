package com.convopal.demo.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(max = 100, message = "Full name must be less than 100 characters")
    private String fullName;
    
    @Size(max = 500, message = "Bio must be less than 500 characters")
    private String bio;
    
    @Size(max = 50, message = "Status must be less than 50 characters")
    private String status;
    
    private String avatarUrl;
} 