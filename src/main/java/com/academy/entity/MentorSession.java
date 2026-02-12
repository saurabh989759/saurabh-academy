package com.academy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Report: DB Schema Design - Mentor_sessions table
 * Report: Feature Development Process - Core entity for "Book a Mentor Session" feature
 * Entity representing a mentor session between a student and mentor
 */
@Entity
@Table(name = "mentor_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Timestamp time;
    
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;
    
    @Column(name = "student_rating")
    private Integer studentRating;
    
    @Column(name = "mentor_rating")
    private Integer mentorRating;
}

