package com.academy.exception;

/**
 * Exception thrown when a Class resource is not found
 */
public class ClassNotFoundException extends ResourceNotFoundException {
    
    public ClassNotFoundException(Long id) {
        super("Class", id);
    }
    
    public ClassNotFoundException(String message) {
        super(message);
    }
}

