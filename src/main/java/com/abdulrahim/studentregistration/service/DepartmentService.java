package com.abdulrahim.studentregistration.service;

import com.abdulrahim.studentregistration.dto.DepartmentRequest;
import com.abdulrahim.studentregistration.dto.DepartmentResponse;
import com.abdulrahim.studentregistration.entity.Department;
import com.abdulrahim.studentregistration.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentService {

   private final DepartmentRepository departmentRepository;

   public DepartmentResponse saveDepartment(DepartmentRequest request) {

      //create department
      Department department = new Department();

      //assign name to the department
      department.setName(request.getName());

      //save the department
      Department savedDepartment = departmentRepository.save(department);

      //create responseDTO from entity
      DepartmentResponse response = new DepartmentResponse();
      response.setId(savedDepartment.getId());
      response.setName(savedDepartment.getName());

      return response;
   }
}
