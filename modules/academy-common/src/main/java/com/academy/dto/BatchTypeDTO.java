package com.academy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchTypeDTO {

    private Long id;

    @NotBlank(message = "Batch type name must not be blank")
    private String name;
}
