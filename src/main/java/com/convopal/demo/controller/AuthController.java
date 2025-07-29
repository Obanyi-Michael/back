package com.convopal.demo.controller;

import com.convopal.demo.dto.ApiResponse;
import com.convopal.demo.dto.AuthResponse;
import com.convopal.demo.dto.LoginRequest;
import com.convopal.demo.dto.SignupRequest;
import com.convopal.demo.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthenticationService authenticationService;
    
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@Valid @RequestBody SignupRequest request) {
        try {
            AuthResponse response = authenticationService.signup(request);
            return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authenticationService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid credentials"));
        }
    }
    
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<AuthResponse.UserDto>> validateToken() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                // Return current user info
                AuthResponse.UserDto userInfo = authenticationService.getCurrentUser();
                return ResponseEntity.ok(ApiResponse.success("Token is valid", userInfo));
            } else {
                return ResponseEntity.status(401).body(ApiResponse.error("Invalid token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Token validation failed"));
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken() {
        try {
            AuthResponse response = authenticationService.refreshToken();
            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Token refresh failed"));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Backend is running"));
    }
    
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test() {
        return ResponseEntity.ok(ApiResponse.success("Authentication endpoint is working"));
    }
}