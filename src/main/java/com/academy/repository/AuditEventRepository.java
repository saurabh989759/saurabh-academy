package com.academy.repository;

import com.academy.entity.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for AuditEvent entity
 */
@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
}

