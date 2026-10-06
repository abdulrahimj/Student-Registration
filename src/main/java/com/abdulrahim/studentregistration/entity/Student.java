package com.abdulrahim.studentregistration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
public class Student {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private String name;

   private Integer age;

   private String email;

   private String photoPath;

   @ManyToOne
   @JoinColumn(name = "department_id")
   private Department department;
}
