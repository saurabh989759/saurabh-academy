package com.academy.exception;

/**
 * Exception thrown when a MentorSession resource is not found
 */
public class MentorSessionNotFoundException extends ResourceNotFoundException {
    
    public MentorSessionNotFoundException(Long id) {
        super("MentorSession", id);
    }
    
    public MentorSessionNotFoundException(String message) {
        super(message);
    }
}

