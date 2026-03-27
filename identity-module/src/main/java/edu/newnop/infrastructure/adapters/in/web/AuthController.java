package edu.newnop.infrastructure.adapters.in.web;

import edu.newnop.application.port.in.LoginUseCase;
import edu.newnop.application.port.in.RegisterUserUseCase;
import edu.newnop.application.port.in.RequestOTPMailUseCase;
import edu.newnop.common.ApiResponse;
import edu.newnop.infrastructure.adapters.in.web.dto.EmailVerificationRequest;
import edu.newnop.infrastructure.adapters.in.web.dto.LoginRequest;
import edu.newnop.infrastructure.adapters.in.web.dto.OTPMailRequest;
import edu.newnop.infrastructure.adapters.in.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final RegisterUserUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final RequestOTPMailUseCase requestOTPMailUseCase;

    @PostMapping("/register")
    public ApiResponse<RegisterUserUseCase.RegistrationResult<RequestOTPMailUseCase.RequestOTPMailResult>> register(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.success(
                201,
                "Sign Up successfully",
                registerUseCase.register(
                        new RegisterUserUseCase.RegisterCommand(
                                request.getName(),
                                request.getEmail(),
                                request.getPassword(),
                                request.getRole()
                        )
                ));

    }

    @PostMapping("/login")
    public ApiResponse<LoginUseCase.LoginResult> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Login successful",
                loginUseCase.login(
                        new LoginUseCase.LoginCommand(
                                request.getEmail(),
                                request.getPassword()
                        ))
        );
    }

    @GetMapping("/send-otp")
    public ApiResponse<RequestOTPMailUseCase.RequestOTPMailResult> requestOtp(@RequestBody @Valid OTPMailRequest request) {
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "OTP sent successfully",
                requestOTPMailUseCase.generateAndSendVerificationMail(
                        new RequestOTPMailUseCase.RequestOTPMailCommand(
                                request.getEmail()
                        )
                )
        );
    }

    @PostMapping("/verify")
    public ApiResponse<Void> verifyEmail(@RequestBody @Valid EmailVerificationRequest request) {
        registerUseCase.verifyEmail(new RegisterUserUseCase.VerifyEmailCommand(
                request.getOtpId(),
                request.getOtpCode()
        ));
        return ApiResponse.success(
                HttpStatus.PERMANENT_REDIRECT.value(),
                "Email verified successfully",
                null
        );
    }
}
