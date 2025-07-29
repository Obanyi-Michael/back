package com.convopal.demo.service;

import com.convopal.demo.dto.ContactDto;
import com.convopal.demo.dto.ContactRequestDto;
import com.convopal.demo.dto.UserProfileDto;
import com.convopal.demo.entity.Contact;
import com.convopal.demo.entity.User;
import com.convopal.demo.repository.ContactRepository;
import com.convopal.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {
    
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final UserProfileService userProfileService;
    
    public ContactDto sendContactRequest(ContactRequestDto request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        User targetUser = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("Target user not found"));
        
        // Check if user is trying to add themselves
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new RuntimeException("Cannot add yourself as a contact");
        }
        
        // Check if contact relationship already exists
        List<Contact> existingContacts = contactRepository.findContactRelationship(currentUser, targetUser);
        if (!existingContacts.isEmpty()) {
            throw new RuntimeException("Contact relationship already exists");
        }
        
        // Create new contact request
        Contact contact = new Contact();
        contact.setUser(currentUser);
        contact.setContact(targetUser);
        contact.setStatus(Contact.ContactStatus.PENDING);
        
        Contact savedContact = contactRepository.save(contact);
        UserProfileDto contactProfile = userProfileService.convertToDto(targetUser);
        
        return ContactDto.fromContact(savedContact, contactProfile);
    }
    
    public List<ContactDto> getContacts() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Contact> contacts = contactRepository.findAcceptedContactsByUser(user);
        
        return contacts.stream()
            .map(contact -> {
                UserProfileDto contactProfile = userProfileService.convertToDto(contact.getContact());
                return ContactDto.fromContact(contact, contactProfile);
            })
            .collect(Collectors.toList());
    }
    
    public List<ContactDto> getPendingRequests() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Contact> pendingRequests = contactRepository.findPendingRequestsForUser(user);
        
        return pendingRequests.stream()
            .map(contact -> {
                // For pending requests, we want to show the profile of the user who sent the request
                // The current user is the 'contact' (receiver), so we get the 'user' (sender) profile
                UserProfileDto senderProfile = userProfileService.convertToDto(contact.getUser());
                return ContactDto.fromContact(contact, senderProfile);
            })
            .collect(Collectors.toList());
    }
    
    public List<ContactDto> getSentRequests() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Use a simpler approach - get all contacts and filter
            List<Contact> allContacts = contactRepository.findAll();
            List<Contact> sentRequests = allContacts.stream()
                .filter(contact -> contact.getUser().getId().equals(user.getId()) && 
                                 contact.getStatus() == Contact.ContactStatus.PENDING)
                .collect(Collectors.toList());
            
            return sentRequests.stream()
                .map(contact -> {
                    // For sent requests, we want to show the profile of the user who will receive the request
                    UserProfileDto receiverProfile = userProfileService.convertToDto(contact.getContact());
                    return ContactDto.fromContact(contact, receiverProfile);
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error getting sent requests: " + e.getMessage(), e);
        }
    }
    
    public ContactDto acceptContactRequest(Long contactId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Contact contact = contactRepository.findById(contactId)
            .orElseThrow(() -> new RuntimeException("Contact request not found"));
        
        // Verify the request is for the current user
        if (!contact.getContact().getId().equals(user.getId())) {
            throw new RuntimeException("Contact request not found");
        }
        
        // Verify the request is pending
        if (contact.getStatus() != Contact.ContactStatus.PENDING) {
            throw new RuntimeException("Contact request is not pending");
        }
        
        contact.setStatus(Contact.ContactStatus.ACCEPTED);
        Contact savedContact = contactRepository.save(contact);
        
        UserProfileDto contactProfile = userProfileService.convertToDto(contact.getUser());
        return ContactDto.fromContact(savedContact, contactProfile);
    }
    
    public void rejectContactRequest(Long contactId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Contact contact = contactRepository.findById(contactId)
            .orElseThrow(() -> new RuntimeException("Contact request not found"));
        
        // Verify the request is for the current user
        if (!contact.getContact().getId().equals(user.getId())) {
            throw new RuntimeException("Contact request not found");
        }
        
        contact.setStatus(Contact.ContactStatus.REJECTED);
        contactRepository.save(contact);
    }
    
    public void removeContact(Long contactId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Contact contact = contactRepository.findById(contactId)
            .orElseThrow(() -> new RuntimeException("Contact not found"));
        
        // Verify the contact belongs to the current user
        if (!contact.getUser().getId().equals(user.getId()) && !contact.getContact().getId().equals(user.getId())) {
            throw new RuntimeException("Contact not found");
        }
        
        contactRepository.delete(contact);
    }
} 