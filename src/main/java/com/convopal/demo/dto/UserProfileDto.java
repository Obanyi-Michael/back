package com.convopal.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private String fullName;
    private String username;
    private String phone;
    private String email;
    private String avatarUrl;
    private String status;
    private String bio;
    private String country;
    private Boolean isVerified;
    private Boolean isOnline;
    private String lastSeen;
} 