package com.academy.repository;

import com.academy.entity.MentorSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for MentorSession entity
 */
@Repository
public interface MentorSessionRepository extends JpaRepository<MentorSession, Long> {
    
    /**
     * Find all mentor sessions for a specific student
     */
    List<MentorSession> findByStudentId(Long studentId);
    
    /**
     * Delete all mentor sessions for a specific student
     */
    void deleteByStudentId(Long studentId);
}

