package com.academy.exception;

import java.net.URI;

/**
 * Constants for exception handling and Problem Details
 */
public final class ExceptionConstants {
    
    private ExceptionConstants() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Base URI for problem detail types
     */
    public static final URI PROBLEM_DETAIL_TYPE = URI.create("https://api.academy.com/problems");
    
    /**
     * Problem detail type for resource not found errors
     */
    public static final URI RESOURCE_NOT_FOUND_TYPE = URI.create(PROBLEM_DETAIL_TYPE + "/resource-not-found");
    
    /**
     * Problem detail type for validation errors
     */
    public static final URI VALIDATION_ERROR_TYPE = URI.create(PROBLEM_DETAIL_TYPE + "/validation-error");
    
    /**
     * Problem detail type for runtime errors
     */
    public static final URI RUNTIME_ERROR_TYPE = URI.create(PROBLEM_DETAIL_TYPE + "/runtime-error");
    
    /**
     * Problem detail type for internal server errors
     */
    public static final URI INTERNAL_ERROR_TYPE = URI.create(PROBLEM_DETAIL_TYPE + "/internal-error");
}

