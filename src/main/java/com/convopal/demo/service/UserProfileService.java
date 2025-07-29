package com.convopal.demo.service;

import com.convopal.demo.dto.UpdateProfileRequest;
import com.convopal.demo.dto.UserProfileDto;
import com.convopal.demo.entity.User;
import com.convopal.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    
    private final UserRepository userRepository;
    
    public UserProfileDto getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return convertToDto(user);
    }
    
    public UserProfileDto getUserProfileById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return convertToDto(user);
    }
    
    public UserProfileDto getUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return convertToDto(user);
    }
    
    public UserProfileDto updateProfile(UpdateProfileRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update fields if provided
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }
    
    public List<UserProfileDto> searchUsers(String query) {
        // Search by username, full name, or phone
        List<User> users = userRepository.findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrPhoneContaining(
            query, query, query);
        
        return users.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public void updateOnlineStatus(boolean isOnline) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsOnline(isOnline);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
    }
    
    private UserProfileDto convertToDto(User user) {
        return new UserProfileDto(
            user.getId(),
            user.getFullName(),
            user.getUsername(),
            user.getPhone(),
            user.getEmail(),
            user.getAvatarUrl(),
            user.getStatus(),
            user.getBio(),
            user.getCountry(),
            user.getIsVerified(),
            user.getIsOnline(),
            user.getLastSeen() != null ? user.getLastSeen().toString() : null
        );
    }
} 