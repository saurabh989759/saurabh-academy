package com.academy.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> onResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource lookup failed: {}", ex.getMessage());
        return ResponseEntity.of(ex.getBody()).build();
    }

    @ExceptionHandler(LockAcquisitionException.class)
    public ResponseEntity<ProblemDetail> onLockAcquisitionFailure(LockAcquisitionException ex) {
        log.warn("Distributed lock could not be acquired: {}", ex.getMessage());
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "Another request is currently processing this resource — please retry shortly."
        );
        detail.setType(ExceptionConstants.LOCK_ERROR_TYPE);
        detail.setTitle("Concurrent Operation Conflict");
        return ResponseEntity.of(detail).build();
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ProblemDetail> onOptimisticLockConflict(OptimisticLockException ex) {
        log.warn("Optimistic concurrency conflict: {}", ex.getMessage());
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "The record was updated by someone else — please reload and try again."
        );
        detail.setType(ExceptionConstants.LOCK_ERROR_TYPE);
        detail.setTitle("Concurrent Modification Detected");
        return ResponseEntity.of(detail).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> onValidationFailure(MethodArgumentNotValidException ex) {
        log.warn("Request validation rejected: {}", ex.getMessage());

        Map<String, String> violations = ex.getBindingResult().getAllErrors().stream()
            .filter(e -> e instanceof FieldError)
            .map(e -> (FieldError) e)
            .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "One or more fields failed validation"
        );
        detail.setType(ExceptionConstants.VALIDATION_ERROR_TYPE);
        detail.setTitle("Validation Error");
        detail.setProperty("errors", violations);
        return ResponseEntity.of(detail).build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> onRuntimeException(RuntimeException ex) {
        log.error("Runtime error encountered: ", ex);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred"
        );
        detail.setType(ExceptionConstants.RUNTIME_ERROR_TYPE);
        detail.setTitle("Runtime Error");
        return ResponseEntity.of(detail).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> onUnhandledException(Exception ex) {
        log.error("Unhandled exception: ", ex);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An internal error occurred — please contact support if the issue persists."
        );
        detail.setType(ExceptionConstants.INTERNAL_ERROR_TYPE);
        detail.setTitle("Internal Server Error");
        return ResponseEntity.of(detail).build();
    }
}
