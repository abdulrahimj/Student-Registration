package com.abdulrahim.studentregistration.service;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StudentFileValidationService {

   private final Tika tika;

   public String validatePhoto(MultipartFile photo) {

      //Validate content type if it is an image type
      String contentType = photo.getContentType();

      if (contentType == null || !contentType.startsWith("image/")) {
         throw new IllegalArgumentException("Only image files are allowed");
      }

      try {
         //Validate the actual content if it is an image
         String detectedType = tika.detect(photo.getInputStream());

         if (!detectedType.startsWith("image/png")
            && !detectedType.equals("image/jpeg")
            && !detectedType.equals("image/webp")) {

            throw new IllegalArgumentException("Only PNG, JPEG, and Webp images are allowed");
         }

         return detectedType;

      } catch (IOException e) {
         throw new IllegalArgumentException("Could not read uploaded file");
      }
   }
}
