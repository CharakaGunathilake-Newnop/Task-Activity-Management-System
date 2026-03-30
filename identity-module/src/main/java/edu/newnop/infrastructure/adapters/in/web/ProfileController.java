package edu.newnop.infrastructure.adapters.in.web;

import edu.newnop.application.port.in.DeleteAccountUseCase;
import edu.newnop.application.port.in.ProfileManagementUseCase;
import edu.newnop.common.ApiResponse;
import edu.newnop.infrastructure.adapters.in.web.dto.DeleteAccountRequest;
import edu.newnop.infrastructure.adapters.in.web.dto.ResetPasswordRequest;
import edu.newnop.infrastructure.adapters.in.web.dto.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileManagementUseCase profileManagementUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;

    @GetMapping("/me")
    public ApiResponse<ProfileManagementUseCase.GetProfileResult> getProfile(UserPrincipal userPrincipal) {
        return
                ApiResponse.success(
                        200,
                        "Profile retrieved successfully",
                        profileManagementUseCase.getProfile(
                                new ProfileManagementUseCase.GetProfileCommand(
                                        userPrincipal.getName()
                                )
                        )
                );

    }

    @PutMapping
    public ApiResponse<ProfileManagementUseCase.UpdateProfileResult> updateProfile(UserPrincipal userPrincipal, @RequestBody UpdateProfileRequest request) {
        return
                ApiResponse.success(
                        200,
                        "Profile updated successfully",
                        profileManagementUseCase.updateProfile(new ProfileManagementUseCase.UpdateProfileCommand(
                                userPrincipal.getName(),
                                request.getEmail(),
                                request.getName()
                        ))
                );

    }

    @PatchMapping("/reset-password")
    public ApiResponse<ProfileManagementUseCase.ChangePasswordResult> changePassword(UserPrincipal userPrincipal, @RequestBody ResetPasswordRequest request){
        return ApiResponse.success(
                200,
                "Password changed successfully",
                profileManagementUseCase.changePassword(new ProfileManagementUseCase.ChangePasswordCommand(
                        userPrincipal.getName(),
                        request.getOldPassword(),
                        request.getNewPassword()
                ))
        );
    }


    @DeleteMapping
    public ApiResponse<DeleteAccountUseCase.DeleteAccountResult> deleteAccount(UserPrincipal userPrincipal, @RequestBody DeleteAccountRequest request) {
        return ApiResponse.success(
                200,
                "Account deleted successfully",
                deleteAccountUseCase.deleteAccount(new DeleteAccountUseCase.DeleteAccountCommand(
                        userPrincipal.getName(),
                        request.getPassword()
                ))
        );
    }
}
