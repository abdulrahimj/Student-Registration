package com.abdulrahim.studentregistration.service;

import com.abdulrahim.studentregistration.dto.PageResponse;
import com.abdulrahim.studentregistration.dto.StudentRequest;
import com.abdulrahim.studentregistration.dto.StudentResponse;
import com.abdulrahim.studentregistration.entity.Department;
import com.abdulrahim.studentregistration.entity.Student;
import com.abdulrahim.studentregistration.exception.ResourceNotFoundException;
import com.abdulrahim.studentregistration.repository.DepartmentRepository;
import com.abdulrahim.studentregistration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class StudentService {

   private final StudentRepository studentRepository;
   private final DepartmentRepository departmentRepository;
   private final StudentFileValidationService studentFileValidationService;
   private final StudentFileStorageService studentFileStorageService;

   @Transactional
   public StudentResponse saveStudent(StudentRequest request, MultipartFile photo) {

      //Validate photo
      studentFileValidationService.validatePhoto(photo);

      //find department
      Department department = departmentRepository
              .findById(request.getDepartmentId())
              .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

      //Store the image
      String photoPath = studentFileStorageService.storePhoto(photo);

      try {
         //create an empty student
         Student student = new Student();

         //copy requestDTO and convert to entity
         student.setName(request.getName());
         student.setAge(request.getAge());
         student.setEmail(request.getEmail());
         student.setDepartment(department);
         student.setPhotoPath(photoPath);

         //Force Hibernate to sync with the DB now and save the entity
         Student savedEntity = studentRepository.saveAndFlush(student);

         //create responseDTO from entity
         StudentResponse response = new StudentResponse();
         response.setId(savedEntity.getId());
         response.setName(savedEntity.getName());
         response.setAge(savedEntity.getAge());
         response.setEmail(savedEntity.getEmail());
         response.setDepartmentName(savedEntity.getDepartment().getName());
         response.setPhotoPath(
                 "/uploads/students/" +
                         Paths.get(savedEntity.getPhotoPath()).getFileName()
         );

         return response;

      } catch (RuntimeException e) {
         //If saving student fails, remove the stored image
         try {
            studentFileStorageService.deletePhoto(photoPath);
         } catch (RuntimeException cleanupException) {
            e.addSuppressed(cleanupException);
         }

         //Preserve the original exception
         throw e;
      }




   }

   public PageResponse<StudentResponse> getAllStudents(Pageable pageable) {

      Page<Student> students = studentRepository.findAll(pageable);

      Page<StudentResponse> responses = students.map(student -> {
         StudentResponse response = new StudentResponse();
         response.setId(student.getId());
         response.setName(student.getName());
         response.setAge(student.getAge());
         response.setDepartmentName(student.getDepartment().getName());

         return response;
      });

      PageResponse<StudentResponse> pageResponse = new PageResponse<>();

      pageResponse.setContent(responses.getContent());
      pageResponse.setPage(responses.getNumber());
      pageResponse.setSize(responses.getSize());
      pageResponse.setTotalElements(responses.getTotalElements());
      pageResponse.setTotalPages(responses.getTotalPages());
      pageResponse.setFirst(responses.isFirst());
      pageResponse.setLast(responses.isLast());

      return pageResponse;
   }
}
