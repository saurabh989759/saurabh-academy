package com.academy.exception;

/**
 * Exception thrown when a BatchType resource is not found
 */
public class BatchTypeNotFoundException extends ResourceNotFoundException {
    
    public BatchTypeNotFoundException(Long id) {
        super("BatchType", id);
    }
    
    public BatchTypeNotFoundException(String message) {
        super(message);
    }
}

