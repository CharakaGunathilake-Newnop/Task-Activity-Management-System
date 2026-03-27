package edu.newnop.application.port.in;

public interface RequestOTPMailUseCase {
    RequestOTPMailResult generateAndSendVerificationMail(RequestOTPMailCommand command);

    record RequestOTPMailCommand(
            String email
    ) {
    }

    record RequestOTPMailResult(
            String otpId,
            String message
    ) {
    }
}
