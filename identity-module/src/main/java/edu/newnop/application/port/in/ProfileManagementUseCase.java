package edu.newnop.application.port.in;

import java.time.Instant;

public interface ProfileManagementUseCase {
    GetProfileResult getProfile(GetProfileCommand command);

    record GetProfileCommand(
            String email
    ) {
    }

    record GetProfileResult(
            String name,
            String email,
            String role,
            boolean isVerified,
            Instant createdAt
    ) {
    }

    UpdateProfileResult updateProfile(UpdateProfileCommand command);

    record UpdateProfileCommand(
            String userId, // to support both email and name update, we need userId (current email) to identify the user
            String email,
            String name
    ) {
    }

    record UpdateProfileResult(
            String name,
            String email,
            String role,
            boolean isVerified,
            Instant createdAt
    ) {
    }

    ChangePasswordResult changePassword(ChangePasswordCommand command);

    record ChangePasswordCommand(
            String email,
            String oldPassword,
            String newPassword
    ) {
    }

    record ChangePasswordResult(
            String message
    ) {
    }
}
