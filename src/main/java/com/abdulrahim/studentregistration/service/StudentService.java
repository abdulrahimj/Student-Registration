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
import org.apache.tika.Tika;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

   private final StudentRepository studentRepository;
   private final DepartmentRepository departmentRepository;
   private final Tika tika;

   @Transactional
   public StudentResponse saveStudent(StudentRequest request, MultipartFile photo) throws IOException {

      //Validate content type if it is an image type
      String contentType = photo.getContentType();

      if (contentType == null || !contentType.startsWith("image/")) {
         throw new IllegalArgumentException("Only image files are allowed");
      }

      //Validate the actual content if it is an image
      String detectedType = tika.detect(photo.getInputStream());

      if (!detectedType.startsWith("image/")) {
         throw new IllegalArgumentException("Upload file is not a valid image");
      }

      //find department
      Department department = departmentRepository
              .findById(request.getDepartmentId())
              .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

      //create an empty student
      Student student = new Student();

      //copy requestDTO and convert to entity
      student.setName(request.getName());
      student.setAge(request.getAge());
      student.setEmail(request.getEmail());
      //set the relationship
      student.setDepartment(department);

      Path uploadPath = Paths.get("uploads/students");
      Files.createDirectories(uploadPath);

      String originalFilename = photo.getOriginalFilename();
      assert originalFilename != null;
      String fileExtension = "";
      int dotIndex = originalFilename.lastIndexOf(".");

      if (dotIndex != -1) {
         fileExtension = originalFilename.substring(dotIndex);
      }

      String fileName = UUID.randomUUID() + fileExtension;
      Path filePath = uploadPath.resolve(fileName);
      photo.transferTo(filePath);

      //connect the file to the student record
      student.setPhotoPath(filePath.toString());

      //save the entity
      Student savedEntity = studentRepository.save(student);

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
