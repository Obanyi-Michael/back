package com.convopal.demo.repository;

import com.convopal.demo.entity.Group;
import com.convopal.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    @Query("SELECT g FROM Group g JOIN g.members m WHERE m = :user AND g.isActive = true")
    List<Group> findGroupsByMember(@Param("user") User user);
    
    @Query("SELECT g FROM Group g WHERE g.createdBy = :user AND g.isActive = true")
    List<Group> findGroupsCreatedBy(@Param("user") User user);
    
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM Group g JOIN g.members m WHERE g.id = :groupId AND m = :user")
    boolean isUserMemberOfGroup(@Param("groupId") Long groupId, @Param("user") User user);
    
    @Query("SELECT CASE WHEN g.createdBy = :user THEN true ELSE false END FROM Group g WHERE g.id = :groupId")
    boolean isUserCreatorOfGroup(@Param("groupId") Long groupId, @Param("user") User user);
    
    Optional<Group> findByNameAndIsActiveTrue(String name);
    
    @Query("SELECT g FROM Group g WHERE g.name LIKE %:query% AND g.isActive = true")
    List<Group> searchGroupsByName(@Param("query") String query);
} 