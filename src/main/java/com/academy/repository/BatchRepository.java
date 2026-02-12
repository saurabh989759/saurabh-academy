package com.academy.repository;

import com.academy.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Batch entity
 */
@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
}

