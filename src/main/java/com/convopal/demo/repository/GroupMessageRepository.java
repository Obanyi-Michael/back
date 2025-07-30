package com.convopal.demo.repository;

import com.convopal.demo.entity.Group;
import com.convopal.demo.entity.GroupMessage;
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
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    
    @Query("SELECT gm FROM GroupMessage gm WHERE gm.group.id = :groupId ORDER BY gm.createdAt DESC")
    Page<GroupMessage> findMessagesByGroupId(@Param("groupId") Long groupId, Pageable pageable);
    
    @Query("SELECT COUNT(gm) FROM GroupMessage gm WHERE gm.group.id = :groupId AND gm.isRead = false AND gm.sender != :user")
    long countUnreadMessagesForUser(@Param("groupId") Long groupId, @Param("user") User user);
    
    @Modifying
    @Query("UPDATE GroupMessage gm SET gm.isRead = true WHERE gm.group.id = :groupId AND gm.sender != :user")
    void markMessagesAsRead(@Param("groupId") Long groupId, @Param("user") User user);
    
    @Query("SELECT gm FROM GroupMessage gm WHERE gm.group.id = :groupId AND gm.isRead = false AND gm.sender != :user ORDER BY gm.createdAt DESC")
    List<GroupMessage> findUnreadMessagesForUser(@Param("groupId") Long groupId, @Param("user") User user);
    
    @Query("SELECT gm FROM GroupMessage gm WHERE gm.group.id = :groupId ORDER BY gm.createdAt DESC LIMIT 1")
    GroupMessage findLatestMessageByGroupId(@Param("groupId") Long groupId);
} 