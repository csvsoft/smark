package com.csvsoft.smark.exception;

public class SmarkRunTimeException  extends RuntimeException{
    public SmarkRunTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmarkRunTimeException(String message) {
        super(message);
    }
}
