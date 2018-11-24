package com.csvsoft.smark.exception;

public class SmarkSparkServiceException extends Exception {
    public SmarkSparkServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmarkSparkServiceException(String message) {
        super(message);
    }
}
