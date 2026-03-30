package edu.newnop.infrastructure.adapters.in.web.exceptions;

public class EmailServiceUnavailableException extends RuntimeException {
    public EmailServiceUnavailableException(String message) {
        super(message);
    }
}
