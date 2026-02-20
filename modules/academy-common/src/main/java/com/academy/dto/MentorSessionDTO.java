package com.academy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorSessionDTO {

    private Long id;

    @NotNull(message = "Session time is required")
    private Instant time;

    @NotNull(message = "Session duration must be specified")
    private Integer durationMinutes;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Mentor ID is required")
    private Long mentorId;

    private Integer studentRating;
    private Integer mentorRating;
}
