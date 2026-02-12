package com.academy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Report: DB Schema Design - Student_batch_history table
 * Entity representing the history of student batch assignments
 */
@Entity
@Table(name = "student_batch_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentBatchHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;
    
    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;
}

