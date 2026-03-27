package edu.newnop.infrastructure.adapters.in.web.exceptions;

public class OtpVerificationFailedException extends RuntimeException {
    public OtpVerificationFailedException(String message) {
        super(message);
    }
}
