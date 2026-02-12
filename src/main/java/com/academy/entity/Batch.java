package com.academy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Report: DB Schema Design - Batches table
 * Entity representing a batch of students
 */
@Entity
@Table(name = "batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Batch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "start_month", nullable = false)
    private LocalDate startMonth;
    
    @Column(name = "current_instructor", nullable = false)
    private String currentInstructor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_type_id", nullable = false)
    private BatchType batchType;
    
    @ManyToMany
    @JoinTable(
        name = "batches_classes",
        joinColumns = @JoinColumn(name = "batch_id"),
        inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    private Set<ClassEntity> classes = new HashSet<>();
    
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL)
    private Set<Student> students = new HashSet<>();
}

