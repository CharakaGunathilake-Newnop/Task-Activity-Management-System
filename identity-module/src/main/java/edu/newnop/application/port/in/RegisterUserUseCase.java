package edu.newnop.application.port.in;

public interface RegisterUserUseCase {
    RegistrationResult register(RegisterCommand command);

    record RegisterCommand(
            String name,
            String email,
            String password,
            String role
    ) {
    }

    record RegistrationResult<T>(
            Long id,
            String email,
            T data
    ) {
    }

    SendMailResult sendVerificationMail(SendMailCommand<?> command);

    record SendMailCommand<T>(
            String receiverEmail,
            String receiverName,
            String subject,
            String message,
            T data
    ) {
    }

    record SendMailResult(
            String message
    ) {
    }



    boolean verifyEmail(VerifyEmailCommand command);

    record VerifyEmailCommand(
            String otpId,
            String otpCode
    ){}
}