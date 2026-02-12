package com.academy.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

/**
 * Base exception for resource not found scenarios
 * Implements ErrorResponse for Spring Problem Details (RFC 7807)
 */
@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException implements ErrorResponse {
    
    private HttpStatusCode statusCode;
    private ProblemDetail body;
    
    public ResourceNotFoundException(String message) {
        super(message);
        this.statusCode = HttpStatusCode.valueOf(404);
        this.body = ProblemDetail.forStatusAndDetail(this.statusCode, message);
        this.body.setType(ExceptionConstants.RESOURCE_NOT_FOUND_TYPE);
        this.body.setTitle("Resource Not Found");
    }
    
    public ResourceNotFoundException(String resourceName, Long id) {
        this(String.format("%s not found with id: %d", resourceName, id));
    }
    
    public ResourceNotFoundException(ProblemDetail problemDetail) {
        super(problemDetail.getDetail());
        this.statusCode = HttpStatusCode.valueOf(problemDetail.getStatus());
        this.body = problemDetail;
        this.body.setType(ExceptionConstants.RESOURCE_NOT_FOUND_TYPE);
    }
}

