package com.abdulrahim.studentregistration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

   @ExceptionHandler(ResourceNotFoundException.class)
   public ResponseEntity<ErrorResponse> handleResourceNotFound(
           ResourceNotFoundException ex) {

     ErrorResponse error = new ErrorResponse();

     error.setStatus(HttpStatus.NOT_FOUND.value());
     error.setMessage(ex.getMessage());
     error.setTimestamp(LocalDateTime.now());

     return ResponseEntity
             .status(HttpStatus.NOT_FOUND)
             .body(error);
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
           MethodArgumentNotValidException ex) {

      Map<String, String> errors = new HashMap<>();

      ex.getBindingResult()
              .getFieldErrors()
              .forEach(error ->
                      errors.put(error.getField(), error.getDefaultMessage()));

      ValidationErrorResponse response = new ValidationErrorResponse();

      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setMessage("Validation failed");
      response.setErrors(errors);
      response.setTimestamp(LocalDateTime.now());

      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(response);
   }

   @ExceptionHandler(IllegalArgumentException.class)
   public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {

      ErrorResponse error = new ErrorResponse();

      error.setStatus(HttpStatus.BAD_REQUEST.value());
      error.setMessage(ex.getMessage());
      error.setTimestamp(LocalDateTime.now());

      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(error);
   }
}
