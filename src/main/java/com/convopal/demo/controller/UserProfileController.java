package com.convopal.demo.controller;

import com.convopal.demo.dto.ApiResponse;
import com.convopal.demo.dto.UpdateProfileRequest;
import com.convopal.demo.dto.UserProfileDto;
import com.convopal.demo.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentUserProfile() {
        try {
            UserProfileDto profile = userProfileService.getCurrentUserProfile();
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserProfileById(@PathVariable Long userId) {
        try {
            UserProfileDto profile = userProfileService.getUserProfileById(userId);
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserProfileByUsername(@PathVariable String username) {
        try {
            UserProfileDto profile = userProfileService.getUserProfileByUsername(username);
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        try {
            UserProfileDto updatedProfile = userProfileService.updateProfile(request);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserProfileDto>>> searchUsers(@RequestParam String query) {
        try {
            List<UserProfileDto> users = userProfileService.searchUsers(query);
            return ResponseEntity.ok(ApiResponse.success("Users found", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/online-status")
    public ResponseEntity<ApiResponse<String>> updateOnlineStatus(@RequestParam boolean isOnline) {
        try {
            userProfileService.updateOnlineStatus(isOnline);
            return ResponseEntity.ok(ApiResponse.success("Online status updated successfully", "Status updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 