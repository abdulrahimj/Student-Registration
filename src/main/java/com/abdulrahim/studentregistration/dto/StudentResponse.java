package com.abdulrahim.studentregistration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

   private Long id;

   private String name;

   private Integer age;

   private String email;

   private String departmentName;

   private String photoPath;
}
