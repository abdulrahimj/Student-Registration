package com.abdulrahim.studentregistration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record StudentPatchRequest(

        @NotBlank
        String name,

        @Min(16)
        Integer age,

        @Email
        String email,

        @Positive
        Long departmentId
) {
}
