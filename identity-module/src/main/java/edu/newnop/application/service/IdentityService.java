package edu.newnop.application.service;

import edu.newnop.application.port.in.*;

public interface IdentityService extends
        RegisterUserUseCase,
        LoginUseCase,
        RequestOTPMailUseCase,
        ProfileManagementUseCase,
        DeleteAccountUseCase {
}
