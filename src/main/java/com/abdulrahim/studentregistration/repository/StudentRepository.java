package com.abdulrahim.studentregistration.repository;

import com.abdulrahim.studentregistration.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
