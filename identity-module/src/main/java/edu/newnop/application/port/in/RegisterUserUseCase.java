package edu.newnop.application.port.in;

public interface RegisterUserUseCase {
    RegistrationResult register(RegisterCommand command);

    record RegisterCommand(
            String name,
            String email,
            String password,
            String role
    ) {}

    record RegistrationResult(
            Long id,
            String email,
            String OtpId
    ) {}
}