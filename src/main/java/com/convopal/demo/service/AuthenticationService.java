package com.convopal.demo.service;

import com.convopal.demo.dto.AuthResponse;
import com.convopal.demo.dto.LoginRequest;
import com.convopal.demo.dto.SignupRequest;
import com.convopal.demo.entity.User;
import com.convopal.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthResponse signup(SignupRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if phone already exists
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }
        
        // Create new user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setCountry(request.getCountry());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsVerified(true); // Auto-verify for simplicity
        
        User savedUser = userRepository.save(user);
        
        // Generate tokens
        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        
        return new AuthResponse(
            accessToken,
            refreshToken,
            new AuthResponse.UserDto(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getUsername(),
                savedUser.getPhone(),
                savedUser.getEmail(),
                savedUser.getAvatarUrl(),
                savedUser.getIsVerified()
            )
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        // Try to authenticate with username or phone
        User user = userRepository.findByUsername(request.getUsernameOrPhone())
            .orElseGet(() -> userRepository.findByPhone(request.getUsernameOrPhone())
                .orElseThrow(() -> new RuntimeException("Invalid credentials")));
        
        // Authenticate
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                request.getPassword()
            )
        );
        
        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return new AuthResponse(
            accessToken,
            refreshToken,
            new AuthResponse.UserDto(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getPhone(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getIsVerified()
            )
        );
    }
}