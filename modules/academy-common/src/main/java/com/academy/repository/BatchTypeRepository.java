package com.academy.repository;

import com.academy.entity.BatchType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for BatchType entity
 */
@Repository
public interface BatchTypeRepository extends JpaRepository<BatchType, Long> {
}

