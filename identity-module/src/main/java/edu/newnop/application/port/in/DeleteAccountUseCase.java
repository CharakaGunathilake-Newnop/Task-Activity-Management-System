package edu.newnop.application.port.in;

public interface DeleteAccountUseCase {
    DeleteAccountResult deleteAccount(DeleteAccountCommand command);

    record DeleteAccountCommand(
            String email,
            String password
    ) {
    }

    record DeleteAccountResult(
            String message
    ) {
    }
}
