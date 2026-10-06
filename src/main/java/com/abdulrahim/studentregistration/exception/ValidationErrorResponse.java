package com.abdulrahim.studentregistration.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ValidationErrorResponse {

   private int status;
   private String message;
   private Map<String, String> errors;
   private LocalDateTime timestamp;
}
