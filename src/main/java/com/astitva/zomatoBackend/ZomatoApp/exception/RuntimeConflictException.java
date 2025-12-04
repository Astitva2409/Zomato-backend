package com.astitva.zomatoBackend.ZomatoApp.exception;

public class RuntimeConflictException extends RuntimeException {
    public RuntimeConflictException() {
    }

    public RuntimeConflictException(String message) {
        super(message);
    }
}
