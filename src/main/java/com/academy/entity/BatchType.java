package com.academy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Report: DB Schema Design - Batch_type table
 * Entity representing a batch type (e.g., "Full Stack", "Data Science")
 */
@Entity
@Table(name = "batch_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
}

