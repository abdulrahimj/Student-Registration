package com.abdulrahim.studentregistration.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentRequest {

   @NotBlank(message = "Department name is required")
   private String name;
}
