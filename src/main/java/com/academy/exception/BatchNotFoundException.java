package com.academy.exception;

/**
 * Exception thrown when a Batch resource is not found
 */
public class BatchNotFoundException extends ResourceNotFoundException {
    
    public BatchNotFoundException(Long id) {
        super("Batch", id);
    }
    
    public BatchNotFoundException(String message) {
        super(message);
    }
}

