package com.abdulrahim.studentregistration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentRequest {

   @NotBlank(message = "Name is required")
   private String name;

   @NotNull(message = "Age is required")
   @Min(value = 16, message = "Age must be at least 16")
   private Integer age;

   @NotBlank(message = "Email is required")
   @Email(message = "Invalid email address")
   private String email;

   @NotNull(message = "Department is required")
   private Long departmentId;
}
