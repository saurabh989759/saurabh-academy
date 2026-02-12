package com.academy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for Batch entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Start month is required")
    private LocalDate startMonth;
    
    @NotBlank(message = "Current instructor is required")
    private String currentInstructor;
    
    @NotNull(message = "Batch type ID is required")
    private Long batchTypeId;
    
    private String batchTypeName;
    private Set<Long> classIds;
}

