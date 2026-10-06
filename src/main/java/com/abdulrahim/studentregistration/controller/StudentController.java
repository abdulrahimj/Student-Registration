package com.abdulrahim.studentregistration.controller;

import com.abdulrahim.studentregistration.dto.PageResponse;
import com.abdulrahim.studentregistration.dto.StudentRequest;
import com.abdulrahim.studentregistration.dto.StudentResponse;
import com.abdulrahim.studentregistration.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

   private final StudentService studentService;

   @PostMapping //(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public ResponseEntity<StudentResponse> saveStudent(
           @Valid @RequestPart("student") StudentRequest request,
           @RequestPart("photo") MultipartFile photo) throws IOException {

      StudentResponse response = studentService.saveStudent(request, photo);

      return ResponseEntity
              .status(HttpStatus.CREATED)
              .body(response);
   }

   @GetMapping
   public ResponseEntity<PageResponse<StudentResponse>> getAllStudents(Pageable pageable) {

      PageResponse<StudentResponse> responses = studentService.getAllStudents(pageable);
      return ResponseEntity.ok(responses);
   }
}
