package com.convopal.demo.service;

import com.convopal.demo.dto.MessageDto;
import com.convopal.demo.dto.SendMessageRequest;
import com.convopal.demo.dto.UserProfileDto;
import com.convopal.demo.entity.Message;
import com.convopal.demo.entity.User;
import com.convopal.demo.repository.MessageRepository;
import com.convopal.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserProfileService userProfileService;
    
    public MessageDto sendMessage(SendMessageRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        User receiver = userRepository.findByUsername(request.getReceiverUsername())
            .orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        // Check if users are contacts (optional - you can remove this if you want to allow messaging anyone)
        // For now, we'll allow messaging anyone
        
        Message message = new Message();
        message.setSender(currentUser);
        message.setReceiver(receiver);
        message.setContent(request.getContent());
        message.setType(request.getType());
        
        Message savedMessage = messageRepository.save(message);
        
        UserProfileDto senderProfile = userProfileService.convertToDto(currentUser);
        UserProfileDto receiverProfile = userProfileService.convertToDto(receiver);
        
        return MessageDto.fromMessage(savedMessage, senderProfile, receiverProfile);
    }
    
    public List<MessageDto> getConversation(String otherUsername, int page, int size) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        User otherUser = userRepository.findByUsername(otherUsername)
            .orElseThrow(() -> new RuntimeException("Other user not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findConversation(currentUser, otherUser, pageable);
        
        return messages.getContent().stream()
            .map(message -> {
                UserProfileDto senderProfile = userProfileService.convertToDto(message.getSender());
                UserProfileDto receiverProfile = userProfileService.convertToDto(message.getReceiver());
                return MessageDto.fromMessage(message, senderProfile, receiverProfile);
            })
            .collect(Collectors.toList());
    }
    
    public void markMessagesAsRead(String senderUsername) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        User sender = userRepository.findByUsername(senderUsername)
            .orElseThrow(() -> new RuntimeException("Sender not found"));
        
        messageRepository.markMessagesAsRead(currentUser, sender);
    }
    
    public Long getUnreadCount(String senderUsername) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        User sender = userRepository.findByUsername(senderUsername)
            .orElseThrow(() -> new RuntimeException("Sender not found"));
        
        return messageRepository.countUnreadMessages(currentUser, sender);
    }
    
    public List<MessageDto> getUnreadMessages() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Message> unreadMessages = messageRepository.findUnreadMessages(currentUser);
        
        return unreadMessages.stream()
            .map(message -> {
                UserProfileDto senderProfile = userProfileService.convertToDto(message.getSender());
                UserProfileDto receiverProfile = userProfileService.convertToDto(message.getReceiver());
                return MessageDto.fromMessage(message, senderProfile, receiverProfile);
            })
            .collect(Collectors.toList());
    }
} 