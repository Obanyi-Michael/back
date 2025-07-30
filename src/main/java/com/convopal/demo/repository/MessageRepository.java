package com.convopal.demo.repository;

import com.convopal.demo.entity.Message;
import com.convopal.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Get conversation between two users
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender = :user1 AND m.receiver = :user2) OR " +
           "(m.sender = :user2 AND m.receiver = :user1) " +
           "ORDER BY m.createdAt ASC")
    Page<Message> findConversation(@Param("user1") User user1, 
                                  @Param("user2") User user2, 
                                  Pageable pageable);
    
    // Get unread messages for a user
    @Query("SELECT m FROM Message m WHERE m.receiver = :user AND m.isRead = false")
    List<Message> findUnreadMessages(@Param("user") User user);
    
    // Get unread count for a specific conversation
    @Query("SELECT COUNT(m) FROM Message m WHERE " +
           "m.receiver = :currentUser AND m.sender = :otherUser AND m.isRead = false")
    Long countUnreadMessages(@Param("currentUser") User currentUser, 
                            @Param("otherUser") User otherUser);
    
    // Get recent conversations for a user
    @Query("SELECT DISTINCT m FROM Message m WHERE " +
           "m.sender = :user OR m.receiver = :user " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findRecentMessages(@Param("user") User user, Pageable pageable);
    
    // Mark messages as read
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE " +
           "m.receiver = :receiver AND m.sender = :sender AND m.isRead = false")
    void markMessagesAsRead(@Param("receiver") User receiver, 
                           @Param("sender") User sender);
} 