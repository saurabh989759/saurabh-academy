package com.academy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for BatchType entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchTypeDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
}

