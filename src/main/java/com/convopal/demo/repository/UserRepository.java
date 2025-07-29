package com.convopal.demo.repository;

import com.convopal.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    
    // Search methods
    List<User> findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrPhoneContaining(
        String username, String fullName, String phone);
}