package com.convopal.demo.service;

import com.convopal.demo.dto.*;
import com.convopal.demo.entity.Group;
import com.convopal.demo.entity.GroupMessage;
import com.convopal.demo.entity.User;
import com.convopal.demo.repository.GroupMessageRepository;
import com.convopal.demo.repository.GroupRepository;
import com.convopal.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    
    private final GroupRepository groupRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final UserRepository userRepository;
    private final UserProfileService userProfileService;
    
    @Transactional
    public ApiResponse<GroupDto> createGroup(CreateGroupRequest request, User currentUser) {
        try {
            // Check if group name already exists
            if (groupRepository.findByNameAndIsActiveTrue(request.getName()).isPresent()) {
                return ApiResponse.error("Group name already exists");
            }
            
            // Find all members
            List<User> members = userRepository.findByUsernameIn(request.getMemberUsernames());
            if (members.size() != request.getMemberUsernames().size()) {
                return ApiResponse.error("Some users not found");
            }
            
            // Add current user to members if not already included
            if (!members.contains(currentUser)) {
                members.add(currentUser);
            }
            
            // Create group
            Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .avatarUrl(request.getAvatarUrl())
                .createdBy(currentUser)
                .members(members.stream().collect(Collectors.toSet()))
                .isActive(true)
                .build();
            
            Group savedGroup = groupRepository.save(group);
            return ApiResponse.success(convertToDto(savedGroup));
                
        } catch (Exception e) {
            return ApiResponse.error("Failed to create group: " + e.getMessage());
        }
    }
    
    public ApiResponse<List<GroupDto>> getUserGroups(User currentUser) {
        try {
            List<Group> groups = groupRepository.findGroupsByMember(currentUser);
            List<GroupDto> groupDtos = groups.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
            
            return ApiResponse.success(groupDtos);
                
        } catch (Exception e) {
            return ApiResponse.error("Failed to get user groups: " + e.getMessage());
        }
    }
    
    public ApiResponse<GroupDto> getGroupById(Long groupId, User currentUser) {
        try {
            Group group = groupRepository.findById(groupId).orElse(null);
            if (group == null || !group.isActive()) {
                return ApiResponse.error("Group not found");
            }
            
            if (!groupRepository.isUserMemberOfGroup(groupId, currentUser)) {
                return ApiResponse.error("You are not a member of this group");
            }
            
            return ApiResponse.success(convertToDto(group));
                
        } catch (Exception e) {
            return ApiResponse.error("Failed to get group: " + e.getMessage());
        }
    }
    
    @Transactional
    public ApiResponse<GroupMessageDto> sendMessage(SendGroupMessageRequest request, User currentUser) {
        try {
            Group group = groupRepository.findById(request.getGroupId()).orElse(null);
            if (group == null || !group.isActive()) {
                return ApiResponse.error("Group not found");
            }
            
            if (!groupRepository.isUserMemberOfGroup(request.getGroupId(), currentUser)) {
                return ApiResponse.error("You are not a member of this group");
            }
            
            GroupMessage message = GroupMessage.builder()
                .group(group)
                .sender(currentUser)
                .content(request.getContent())
                .type(GroupMessage.MessageType.valueOf(request.getType().toUpperCase()))
                .isRead(false)
                .build();
            
            GroupMessage savedMessage = groupMessageRepository.save(message);
            return ApiResponse.success(convertToMessageDto(savedMessage));
                
        } catch (Exception e) {
            return ApiResponse.error("Failed to send message: " + e.getMessage());
        }
    }
    
    public ApiResponse<List<GroupMessageDto>> getGroupMessages(Long groupId, int page, int size, User currentUser) {
        try {
            if (!groupRepository.isUserMemberOfGroup(groupId, currentUser)) {
                return ApiResponse.error("You are not a member of this group");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<GroupMessage> messages = groupMessageRepository.findMessagesByGroupId(groupId, pageable);
            
            List<GroupMessageDto> messageDtos = messages.getContent().stream()
                .map(this::convertToMessageDto)
                .collect(Collectors.toList());
            
            return ApiResponse.success(messageDtos);
                
        } catch (Exception e) {
            return ApiResponse.error("Failed to get messages: " + e.getMessage());
        }
    }
    
    @Transactional
    public ApiResponse<Void> markMessagesAsRead(Long groupId, User currentUser) {
        try {
            if (!groupRepository.isUserMemberOfGroup(groupId, currentUser)) {
                return ApiResponse.error("You are not a member of this group");
            }
            
            groupMessageRepository.markMessagesAsRead(groupId, currentUser);
            return ApiResponse.success(null);
                
        } catch (Exception e) {
            return ApiResponse.error("Failed to mark messages as read: " + e.getMessage());
        }
    }
    
    public ApiResponse<Long> getUnreadCount(Long groupId, User currentUser) {
        try {
            if (!groupRepository.isUserMemberOfGroup(groupId, currentUser)) {
                return ApiResponse.error("You are not a member of this group");
            }
            
            long count = groupMessageRepository.countUnreadMessagesForUser(groupId, currentUser);
            return ApiResponse.success(count);
                
        } catch (Exception e) {
            return ApiResponse.error("Failed to get unread count: " + e.getMessage());
        }
    }
    
    private GroupDto convertToDto(Group group) {
        List<UserProfileDto> memberDtos = group.getMembers().stream()
            .map(userProfileService::convertToDto)
            .collect(Collectors.toList());
        
        GroupMessage lastMessage = groupMessageRepository.findLatestMessageByGroupId(group.getId());
        GroupMessageDto lastMessageDto = lastMessage != null ? convertToMessageDto(lastMessage) : null;
        
        return GroupDto.builder()
            .id(group.getId())
            .name(group.getName())
            .description(group.getDescription())
            .avatarUrl(group.getAvatarUrl())
            .createdBy(userProfileService.convertToDto(group.getCreatedBy()))
            .members(memberDtos)
            .isActive(group.isActive())
            .createdAt(group.getCreatedAt())
            .updatedAt(group.getUpdatedAt())
            .memberCount(memberDtos.size())
            .lastMessage(lastMessageDto)
            .build();
    }
    
    private GroupMessageDto convertToMessageDto(GroupMessage message) {
        return GroupMessageDto.builder()
            .id(message.getId())
            .groupId(message.getGroup().getId())
            .sender(userProfileService.convertToDto(message.getSender()))
            .content(message.getContent())
            .type(message.getType().name())
            .isRead(message.isRead())
            .createdAt(message.getCreatedAt())
            .updatedAt(message.getUpdatedAt())
            .build();
    }
} 