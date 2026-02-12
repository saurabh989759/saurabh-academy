package com.academy.repository;

import com.academy.entity.MentorSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for MentorSession entity
 */
@Repository
public interface MentorSessionRepository extends JpaRepository<MentorSession, Long> {
}

