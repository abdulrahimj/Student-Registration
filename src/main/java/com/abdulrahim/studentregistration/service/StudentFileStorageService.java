package com.abdulrahim.studentregistration.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StudentFileStorageService {

   public String storePhoto(MultipartFile photo, String detectedType) {
      try {
         Path uploadPath = Paths.get("uploads/students");
         Files.createDirectories(uploadPath);

         String fileExtension = switch (detectedType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException(
                    "Unsupported image type"
            );
         };

         String fileName = UUID.randomUUID() + fileExtension;
         Path filePath = uploadPath.resolve(fileName);
         photo.transferTo(filePath);

         return filePath.toString();

      } catch (IOException e) {
         throw new IllegalArgumentException("Could not store uploaded file");
      }
   }

   //Delete photo if student creation failed to save to DB
   public void deletePhoto(String photoPath) {

      try {
         Path filePath = Paths.get(photoPath);
         Files.deleteIfExists(filePath);

      } catch (IOException e) {
         throw new IllegalArgumentException("Could not delete uploaded file");
      }
   }
}
