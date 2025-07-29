package com.convopal.demo.controller;

import com.convopal.demo.dto.ApiResponse;
import com.convopal.demo.dto.ContactDto;
import com.convopal.demo.dto.ContactRequestDto;
import com.convopal.demo.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContactController {
    
    private final ContactService contactService;
    
    @GetMapping("/debug")
    public ResponseEntity<ApiResponse<String>> debugContacts() {
        try {
            // This is just for debugging - remove in production
            return ResponseEntity.ok(ApiResponse.success("Contacts debug endpoint", "Contacts table exists"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Debug error: " + e.getMessage()));
        }
    }
    
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<ContactDto>> sendContactRequest(@Valid @RequestBody ContactRequestDto request) {
        try {
            ContactDto contact = contactService.sendContactRequest(request);
            return ResponseEntity.ok(ApiResponse.success("Contact request sent successfully", contact));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactDto>>> getContacts() {
        try {
            List<ContactDto> contacts = contactService.getContacts();
            return ResponseEntity.ok(ApiResponse.success("Contacts retrieved successfully", contacts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<ContactDto>>> getPendingRequests() {
        try {
            List<ContactDto> pendingRequests = contactService.getPendingRequests();
            return ResponseEntity.ok(ApiResponse.success("Pending requests retrieved successfully", pendingRequests));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<List<ContactDto>>> getSentRequests() {
        try {
            List<ContactDto> sentRequests = contactService.getSentRequests();
            return ResponseEntity.ok(ApiResponse.success("Sent requests retrieved successfully", sentRequests));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/{contactId}/accept")
    public ResponseEntity<ApiResponse<ContactDto>> acceptContactRequest(@PathVariable Long contactId) {
        try {
            ContactDto contact = contactService.acceptContactRequest(contactId);
            return ResponseEntity.ok(ApiResponse.success("Contact request accepted successfully", contact));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/{contactId}/reject")
    public ResponseEntity<ApiResponse<String>> rejectContactRequest(@PathVariable Long contactId) {
        try {
            contactService.rejectContactRequest(contactId);
            return ResponseEntity.ok(ApiResponse.success("Contact request rejected successfully", "Request rejected"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{contactId}")
    public ResponseEntity<ApiResponse<String>> removeContact(@PathVariable Long contactId) {
        try {
            contactService.removeContact(contactId);
            return ResponseEntity.ok(ApiResponse.success("Contact removed successfully", "Contact removed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 