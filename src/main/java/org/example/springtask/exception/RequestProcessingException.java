package org.example.springtask.exception;

public class RequestProcessingException extends RuntimeException {
    public RequestProcessingException() {
        super();
    }

    public RequestProcessingException(String message) {
        super(message);
    }

    public RequestProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestProcessingException(Throwable cause) {
        super(cause);
    }
}
