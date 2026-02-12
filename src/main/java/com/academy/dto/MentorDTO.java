package com.academy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Mentor entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String currentCompany;
}

