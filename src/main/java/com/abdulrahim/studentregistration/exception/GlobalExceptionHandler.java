package com.abdulrahim.studentregistration.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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

   @ExceptionHandler(MaxUploadSizeExceededException.class)
   public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(
           MaxUploadSizeExceededException ex) {

      System.out.println("CUSTOM UPLOAD SIZE HANDLER EXECUTED");
      ErrorResponse error = new ErrorResponse(
              HttpStatus.CONTENT_TOO_LARGE.value(),
              "Upload exceeds the allowed size limit",
              LocalDateTime.now()
      );

      return ResponseEntity
              .status(HttpStatus.CONTENT_TOO_LARGE)
              .body(error);
   }

   @ExceptionHandler(Exception.class)
   public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {

      log.error("Unexpected error occurred", ex);

      ErrorResponse error = new ErrorResponse(
              HttpStatus.INTERNAL_SERVER_ERROR.value(),
              "An unexpected error occurred",
              LocalDateTime.now()
      );

      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(error);
   }
}
