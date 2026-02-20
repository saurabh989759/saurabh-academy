package com.academy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {

    private Long id;

    @NotBlank(message = "Student name must not be blank")
    private String name;

    @NotBlank(message = "Email address is mandatory")
    @Email(message = "Provide a valid email address")
    private String email;

    private String phoneNumber;
    private String universityName;
    private Integer graduationYear;
    private Long batchId;
    private Long buddyId;
}
