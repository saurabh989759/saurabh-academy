package com.academy.repository;

import com.academy.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Mentor entity
 */
@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long> {
}

