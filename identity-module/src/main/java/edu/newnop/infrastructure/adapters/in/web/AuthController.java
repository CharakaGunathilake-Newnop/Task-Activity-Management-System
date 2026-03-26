package edu.newnop.infrastructure.adapters.in.web;

import edu.newnop.application.port.in.LoginUseCase;
import edu.newnop.application.port.in.RegisterUserUseCase;
import edu.newnop.common.ApiResponse;
import edu.newnop.infrastructure.adapters.in.web.dto.LoginRequest;
import edu.newnop.infrastructure.adapters.in.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final RegisterUserUseCase registerUseCase;
    private final LoginUseCase loginUseCase;

    @PostMapping("/register")
    public ApiResponse<RegisterUserUseCase.RegistrationResult> register(@RequestBody @Valid RegisterRequest request) {
        var result = registerUseCase.register(
                new RegisterUserUseCase.RegisterCommand(
                        request.getName(),
                        request.getEmail(),
                        request.getPassword(),
                        request.getRole()
                )
        );
        String message = request.getRole() + " created successfully";
        return ApiResponse.success(201, message, result);

    }

    @PostMapping("/login")
    public ApiResponse<LoginUseCase.LoginResult> login(@RequestBody LoginRequest request) {
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
}
