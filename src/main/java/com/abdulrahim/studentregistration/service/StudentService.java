package com.abdulrahim.studentregistration.service;

import com.abdulrahim.studentregistration.dto.PageResponse;
import com.abdulrahim.studentregistration.dto.StudentPatchRequest;
import com.abdulrahim.studentregistration.dto.StudentRequest;
import com.abdulrahim.studentregistration.dto.StudentResponse;
import com.abdulrahim.studentregistration.entity.Department;
import com.abdulrahim.studentregistration.entity.Student;
import com.abdulrahim.studentregistration.exception.ResourceNotFoundException;
import com.abdulrahim.studentregistration.repository.DepartmentRepository;
import com.abdulrahim.studentregistration.repository.StudentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Paths;

@Slf4j
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
      String detectedType = studentFileValidationService.validatePhoto(photo);

      //find department
      Department department = findDepartment(request.getDepartmentId());

      //Store the image
      String photoPath = studentFileStorageService.storePhoto(photo, detectedType);

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

   @Transactional
   public void deleteStudent(Long studentId) {

      //find student
      Student student = findStudent(studentId);

      //get the photo path before deleting
      String photoPath = student.getPhotoPath();

      //delete the student from DB
      studentRepository.delete(student);

      //schedule photo deletion after the DB transaction commits
      //assert photoPath != null;
      if (photoPath != null && !photoPath.isBlank()) {

         TransactionSynchronizationManager.registerSynchronization(
                 new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                       try {
                          studentFileStorageService.deletePhoto(photoPath);
                       } catch (RuntimeException ex) {
                          log.error("Student was deleted, but photo cleanup failed: {}",
                                  photoPath,
                                  ex
                          );
                       }
                    }
                 }
         );
      }
   }

   @Transactional
   public StudentResponse updateStudent(Long studentId, StudentRequest request) {

      //find student
      Student student = findStudent(studentId);

      //find the requested department
      Department department = findDepartment(request.getDepartmentId());

      //update the student details
      student.setName(request.getName());
      student.setAge(request.getAge());
      student.setEmail(request.getEmail());
      student.setDepartment(department);

      //save the updated student
      Student updatedStudent = studentRepository.save(student);

      //return updated data
      return new StudentResponse(
              updatedStudent.getId(),
              updatedStudent.getName(),
              updatedStudent.getAge(),
              updatedStudent.getEmail(),
              updatedStudent.getDepartment().getName(),
              updatedStudent.getPhotoPath()
      );
   }

   @Transactional
   public StudentResponse patchStudent(Long studentId, @Valid StudentPatchRequest request) {

      Student student = findStudent(studentId);

      //update name only if the client provides it
      if (request.name() != null) {
         if (request.name().isBlank()) {
            throw new ResponseStatusException(
                     HttpStatus.BAD_REQUEST,
                    "Name must not be blank");
         }

         student.setName(request.name());
      }

      //update age only if the client provides it
      if (request.age() != null) {
         student.setAge(request.age());
      }

      //update email only if the client provides it
      if (request.email() != null) {
         if (request.email().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email must not be blank");
         }

         student.setEmail(request.email());
      }

      //change department only if a department ID was provided
      if (request.departmentId() != null) {
         Department department = findDepartment(request.departmentId());
         student.setDepartment(department);
      }

      //Reject an empty PATCH request
      if (request.name() == null
              && request.age() == null
              && request.email() == null
              && request.departmentId() == null) {

         throw new ResponseStatusException(
                 HttpStatus.BAD_REQUEST,
                 "At least one field must be provided");
      }

      Student updatedStudent = studentRepository.save(student);

      return new StudentResponse(
              updatedStudent.getId(),
              updatedStudent.getName(),
              updatedStudent.getAge(),
              updatedStudent.getEmail(),
              updatedStudent.getDepartment().getName(),
              updatedStudent.getPhotoPath()
      );
   }

   //============================================
   //HELPER METHODS
   //============================================
   public Student findStudent(Long studentId) {

      return studentRepository.findById(studentId)
              .orElseThrow(() ->
                      new ResourceNotFoundException(
                              "Student not found with id: " + studentId
                      )
              );
   }

   public Department findDepartment(Long departmentId) {

      return departmentRepository.findById(departmentId)
              .orElseThrow(() ->
                      new ResourceNotFoundException(
                              "Department not found with id: " + departmentId
                      )
              );
   }
}
