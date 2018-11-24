package com.csvsoft.smark.exception;

public class SmarkAppNotReadyException extends SmarkSparkServiceException {
    public SmarkAppNotReadyException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmarkAppNotReadyException(String message) {
        super(message);
    }
}
