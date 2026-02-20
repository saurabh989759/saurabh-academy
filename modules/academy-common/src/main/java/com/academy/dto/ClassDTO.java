package com.academy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassDTO {

    private Long id;

    @NotBlank(message = "Class name must not be blank")
    private String name;

    @NotBlank(message = "Instructor name is required")
    private String instructor;

    @NotNull(message = "Class date must be provided")
    private LocalDate date;

    @NotNull(message = "Class time must be provided")
    private LocalTime time;
}
