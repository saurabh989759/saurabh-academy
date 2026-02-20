package com.academy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorDTO {

    private Long id;

    @NotBlank(message = "Mentor name must not be blank")
    private String name;

    private String currentCompany;
}
