package com.academy.exception;

/**
 * Exception thrown when a Student resource is not found
 */
public class StudentNotFoundException extends ResourceNotFoundException {
    
    public StudentNotFoundException(Long id) {
        super("Student", id);
    }
    
    public StudentNotFoundException(String message) {
        super(message);
    }
}

