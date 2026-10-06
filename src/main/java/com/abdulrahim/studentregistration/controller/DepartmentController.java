package com.abdulrahim.studentregistration.controller;

import com.abdulrahim.studentregistration.dto.DepartmentRequest;
import com.abdulrahim.studentregistration.dto.DepartmentResponse;
import com.abdulrahim.studentregistration.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

   private final DepartmentService departmentService;

   @PostMapping
   public ResponseEntity<DepartmentResponse> saveDepartment(
           @Valid @RequestBody DepartmentRequest request) {

      DepartmentResponse response = departmentService.saveDepartment(request);

      return ResponseEntity
              .status(HttpStatus.CREATED)
              .body(response);
   }
}
