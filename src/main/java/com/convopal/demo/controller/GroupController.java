package com.convopal.demo.controller;

import com.convopal.demo.dto.*;
import com.convopal.demo.entity.User;
import com.convopal.demo.service.AuthenticationService;
import com.convopal.demo.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {
    
    private final GroupService groupService;
    private final AuthenticationService authenticationService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<GroupDto>> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        User currentUser = authenticationService.getCurrentUserEntity();
        ApiResponse<GroupDto> response = groupService.createGroup(request, currentUser);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupDto>>> getUserGroups() {
        User currentUser = authenticationService.getCurrentUserEntity();
        ApiResponse<List<GroupDto>> response = groupService.getUserGroups(currentUser);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupDto>> getGroupById(@PathVariable Long groupId) {
        User currentUser = authenticationService.getCurrentUserEntity();
        ApiResponse<GroupDto> response = groupService.getGroupById(groupId, currentUser);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/messages")
    public ResponseEntity<ApiResponse<GroupMessageDto>> sendMessage(@Valid @RequestBody SendGroupMessageRequest request) {
        User currentUser = authenticationService.getCurrentUserEntity();
        ApiResponse<GroupMessageDto> response = groupService.sendMessage(request, currentUser);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{groupId}/messages")
    public ResponseEntity<ApiResponse<List<GroupMessageDto>>> getGroupMessages(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        User currentUser = authenticationService.getCurrentUserEntity();
        ApiResponse<List<GroupMessageDto>> response = groupService.getGroupMessages(groupId, page, size, currentUser);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{groupId}/messages/read")
    public ResponseEntity<ApiResponse<Void>> markMessagesAsRead(@PathVariable Long groupId) {
        User currentUser = authenticationService.getCurrentUserEntity();
        ApiResponse<Void> response = groupService.markMessagesAsRead(groupId, currentUser);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{groupId}/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable Long groupId) {
        User currentUser = authenticationService.getCurrentUserEntity();
        ApiResponse<Long> response = groupService.getUnreadCount(groupId, currentUser);
        return ResponseEntity.ok(response);
    }
} 