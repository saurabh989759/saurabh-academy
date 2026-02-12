package com.academy.exception;

/**
 * Exception thrown when optimistic locking fails (concurrent modification detected)
 */
public class OptimisticLockException extends RuntimeException {
    
    public OptimisticLockException(String message) {
        super(message);
    }
    
    public OptimisticLockException(String message, Throwable cause) {
        super(message, cause);
    }
}

