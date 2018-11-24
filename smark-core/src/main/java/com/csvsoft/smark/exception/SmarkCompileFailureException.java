package com.csvsoft.smark.exception;

public class SmarkCompileFailureException extends SmarkSparkServiceException {
    public SmarkCompileFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmarkCompileFailureException(String message) {
        super(message);
    }
}
