package com.convopal.demo.dto;

import com.convopal.demo.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {
    private Long id;
    private UserProfileDto contact;
    private ContactStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum ContactStatus {
        PENDING, ACCEPTED, REJECTED, BLOCKED
    }
    
    public static ContactDto fromContact(Contact contact, UserProfileDto contactProfile) {
        return new ContactDto(
            contact.getId(),
            contactProfile,
            ContactStatus.valueOf(contact.getStatus().name()),
            contact.getCreatedAt(),
            contact.getUpdatedAt()
        );
    }
} 