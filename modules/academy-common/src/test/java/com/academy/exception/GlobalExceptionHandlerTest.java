package com.academy.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for GlobalExceptionHandler
 * Covers all exception handlers and response scenarios
 */
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {
    
    private GlobalExceptionHandler exceptionHandler;
    
    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }
    
    @Test
    @DisplayName("Should handle ResourceNotFoundException")
    void handleResourceNotFoundException_Returns404() {
        // Given
        StudentNotFoundException ex = new StudentNotFoundException(999L);
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleResourceNotFoundException(ex);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
    }
    
    @Test
    @DisplayName("Should handle LockAcquisitionException")
    void handleLockAcquisitionException_Returns409() {
        // Given
        LockAcquisitionException ex = new LockAcquisitionException("Lock failed");
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleLockAcquisitionException(ex);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getType().toString()).contains(ExceptionConstants.LOCK_ERROR_TYPE.toString());
    }
    
    @Test
    @DisplayName("Should handle OptimisticLockException")
    void handleOptimisticLockException_Returns409() {
        // Given
        OptimisticLockException ex = new OptimisticLockException("Version conflict");
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleOptimisticLockException(ex);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
    }
    
    @Test
    @DisplayName("Should handle RuntimeException")
    void handleRuntimeException_Returns400() {
        // Given
        RuntimeException ex = new RuntimeException("Runtime error");
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleRuntimeException(ex);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getType().toString()).contains(ExceptionConstants.RUNTIME_ERROR_TYPE.toString());
    }
    
    @Test
    @DisplayName("Should handle RuntimeException with null message")
    void handleRuntimeException_WithNullMessage_Returns400() {
        // Given
        RuntimeException ex = new RuntimeException();
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleRuntimeException(ex);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
    }
    
    @Test
    @DisplayName("Should handle MethodArgumentNotValidException")
    void handleValidationExceptions_Returns400() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("student", "email", "Email is required");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleValidationExceptions(ex);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getType().toString()).contains(ExceptionConstants.VALIDATION_ERROR_TYPE.toString());
    }
    
    @Test
    @DisplayName("Should handle generic Exception")
    void handleGenericException_Returns500() {
        // Given
        Exception ex = new Exception("Generic error");
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleGenericException(ex);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getType().toString()).contains(ExceptionConstants.INTERNAL_ERROR_TYPE.toString());
    }
}

