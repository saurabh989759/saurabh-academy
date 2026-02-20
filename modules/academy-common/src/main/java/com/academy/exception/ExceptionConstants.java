package com.academy.exception;

import java.net.URI;

public final class ExceptionConstants {

    private ExceptionConstants() {}

    private static final URI BASE_URI = URI.create("https://api.academy.com/problems");

    public static final URI RESOURCE_NOT_FOUND_TYPE = BASE_URI.resolve("/problems/resource-not-found");
    public static final URI VALIDATION_ERROR_TYPE   = BASE_URI.resolve("/problems/validation-error");
    public static final URI RUNTIME_ERROR_TYPE      = BASE_URI.resolve("/problems/runtime-error");
    public static final URI INTERNAL_ERROR_TYPE     = BASE_URI.resolve("/problems/internal-error");
    public static final URI LOCK_ERROR_TYPE         = BASE_URI.resolve("/problems/lock-error");
}
