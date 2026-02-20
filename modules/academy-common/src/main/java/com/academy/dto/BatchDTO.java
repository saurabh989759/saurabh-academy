package com.academy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchDTO {

    private Long id;

    @NotBlank(message = "Batch name must not be blank")
    private String name;

    @NotBlank(message = "Instructor name is required")
    private String currentInstructor;

    @NotNull(message = "Start month must be specified")
    private LocalDate startMonth;

    @NotNull(message = "Batch type must be specified")
    private Long batchTypeId;

    private String batchTypeName;
    private Set<Long> classIds;
}
