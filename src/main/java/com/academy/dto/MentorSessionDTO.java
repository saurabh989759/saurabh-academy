package com.academy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for MentorSession entity
 * Report: Feature Development Process - DTO used in "Book a Mentor Session" API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorSessionDTO {
    private Long id;
    
    @NotNull(message = "Time is required")
    private Instant time;
    
    @NotNull(message = "Duration is required")
    private Integer durationMinutes;
    
    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    @NotNull(message = "Mentor ID is required")
    private Long mentorId;
    
    private Integer studentRating;
    private Integer mentorRating;
}

