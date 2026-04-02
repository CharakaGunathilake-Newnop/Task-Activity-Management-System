package edu.newnop.infrastructure.adapters.out.notifications.exceptions;

public class EmailServiceUnavailableException extends RuntimeException {
    public EmailServiceUnavailableException(String message) {
        super(message);
    }

    public EmailServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
