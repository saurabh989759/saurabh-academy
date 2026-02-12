package com.academy.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler using Spring Problem Details (RFC 7807)
 * Report: Logging & error handling - Centralized exception handling
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.of(ex.getBody()).build();
    }
    
    @ExceptionHandler(LockAcquisitionException.class)
    public ResponseEntity<ProblemDetail> handleLockAcquisitionException(LockAcquisitionException ex) {
        log.warn("Lock acquisition failed: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "Resource is currently being processed by another request. Please try again."
        );
        problemDetail.setType(ExceptionConstants.LOCK_ERROR_TYPE);
        problemDetail.setTitle("Concurrent Operation Conflict");
        return ResponseEntity.of(problemDetail).build();
    }
    
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ProblemDetail> handleOptimisticLockException(OptimisticLockException ex) {
        log.warn("Optimistic lock failure: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "The resource was modified by another user. Please refresh and try again."
        );
        problemDetail.setType(ExceptionConstants.LOCK_ERROR_TYPE);
        problemDetail.setTitle("Concurrent Modification Detected");
        return ResponseEntity.of(problemDetail).build();
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, 
            ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred"
        );
        problemDetail.setType(ExceptionConstants.RUNTIME_ERROR_TYPE);
        problemDetail.setTitle("Runtime Error");
        return ResponseEntity.of(problemDetail).build();
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Validation failed for one or more fields"
        );
        problemDetail.setType(ExceptionConstants.VALIDATION_ERROR_TYPE);
        problemDetail.setTitle("Validation Error");
        problemDetail.setProperty("errors", fieldErrors);
        
        return ResponseEntity.of(problemDetail).build();
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        log.error("Unexpected exception: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An internal server error occurred"
        );
        problemDetail.setType(ExceptionConstants.INTERNAL_ERROR_TYPE);
        problemDetail.setTitle("Internal Server Error");
        return ResponseEntity.of(problemDetail).build();
    }
}

