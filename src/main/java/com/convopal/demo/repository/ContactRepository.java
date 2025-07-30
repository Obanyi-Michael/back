package com.convopal.demo.repository;

import com.convopal.demo.entity.Contact;
import com.convopal.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    // Find all contacts for a user (accepted friends)
    @Query("SELECT c FROM Contact c WHERE c.user = :user AND c.status = 'ACCEPTED'")
    List<Contact> findAcceptedContactsByUser(@Param("user") User user);
    
    // Find all accepted contacts for a user (both as sender and receiver)
    @Query("SELECT c FROM Contact c WHERE (c.user = :user OR c.contact = :user) AND c.status = 'ACCEPTED'")
    List<Contact> findAcceptedContactsForUser(@Param("user") User user);
    
    // Find all pending requests for a user
    @Query("SELECT c FROM Contact c WHERE c.contact = :user AND c.status = 'PENDING'")
    List<Contact> findPendingRequestsForUser(@Param("user") User user);
    
    // Find all sent requests by a user
    @Query("SELECT c FROM Contact c WHERE c.user = :user AND c.status = 'PENDING'")
    List<Contact> findPendingRequestsByUser(@Param("user") User user);
    
    // Check if contact relationship exists
    Optional<Contact> findByUserAndContact(User user, User contact);
    
    // Check if contact relationship exists in either direction
    @Query("SELECT c FROM Contact c WHERE (c.user = :user1 AND c.contact = :user2) OR (c.user = :user2 AND c.contact = :user1)")
    List<Contact> findContactRelationship(@Param("user1") User user1, @Param("user2") User user2);
    
    // Find all contacts (both directions) for a user
    @Query("SELECT c FROM Contact c WHERE c.user = :user OR c.contact = :user")
    List<Contact> findAllContactsForUser(@Param("user") User user);
} 