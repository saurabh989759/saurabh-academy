package com.academy.repository;

import com.academy.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for ClassEntity
 */
@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
}

