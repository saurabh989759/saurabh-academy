package com.academy.exception;

/**
 * Exception thrown when a Mentor resource is not found
 */
public class MentorNotFoundException extends ResourceNotFoundException {
    
    public MentorNotFoundException(Long id) {
        super("Mentor", id);
    }
    
    public MentorNotFoundException(String message) {
        super(message);
    }
}

