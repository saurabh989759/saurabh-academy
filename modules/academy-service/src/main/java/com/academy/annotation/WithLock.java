package com.academy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Custom annotation for distributed locking
 * Automatically acquires and releases locks using AOP
 * 
 * Usage:
 * <pre>
 * {@code
 * @WithLock(key = "student:onboarding:#{#dto.email}", timeout = 30)
 * public StudentDTO createStudent(StudentDTO dto) {
 *     // Lock is automatically acquired and released
 * }
 * }
 * </pre>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithLock {
    
    /**
     * Lock key expression (supports SpEL)
     * Examples:
     * - "student:onboarding:#{#dto.email}"
     * - "batch:update:#{#id}"
     * - "student:update:email:#{#dto.email}"
     */
    String key();
    
    /**
     * Lock timeout in seconds (default: 30)
     */
    int timeout() default 30;
    
    /**
     * Maximum number of retry attempts (default: 3)
     */
    int maxRetries() default 3;
    
    /**
     * Wait timeout in seconds - maximum time to wait for lock (default: 10)
     */
    int waitTimeout() default 10;
    
    /**
     * Time unit for timeout values (default: SECONDS)
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    
    /**
     * Whether to throw exception if lock cannot be acquired (default: true)
     * If false, method execution is skipped when lock cannot be acquired
     */
    boolean throwOnFailure() default true;
    
    /**
     * Custom error message when lock acquisition fails
     */
    String errorMessage() default "Could not acquire lock. Resource is currently being processed.";
}

