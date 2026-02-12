package com.academy.repository;

import com.academy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username (email)
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Check if user exists by username
     */
    boolean existsByUsername(String username);
}

