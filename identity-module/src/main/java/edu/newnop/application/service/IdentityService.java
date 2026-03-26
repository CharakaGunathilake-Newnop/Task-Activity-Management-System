package edu.newnop.application.service;

import edu.newnop.application.port.in.LoginUseCase;
import edu.newnop.application.port.in.RegisterUserUseCase;
import edu.newnop.application.port.out.PasswordEncoderPort;
import edu.newnop.application.port.out.UserRepositoryPort;
import edu.newnop.domain.model.User;
import edu.newnop.domain.model.UserRole;
import edu.newnop.domain.model.UserStatus;
import edu.newnop.infrastructure.adapters.in.web.exceptions.InvalidUserRoleException;
import edu.newnop.infrastructure.adapters.in.web.exceptions.UserAlreadyExistsException;
import edu.newnop.infrastructure.adapters.out.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class IdentityService implements RegisterUserUseCase, LoginUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private JwtService jwtService;


    @Override
    public LoginResult login(LoginCommand command) {
        return null;
    }

    @Override
    public RegistrationResult register(RegisterCommand command) {
        String email = command.email().trim();
        String role = command.role().trim();

        if (!role.equalsIgnoreCase("user") && !role.equalsIgnoreCase("admin")){
            throw new InvalidUserRoleException("Not a valid user role provided please try User or Admin");
        }

        if (userRepositoryPort.existsByEmail(email)){
            throw new UserAlreadyExistsException("User with Email:"+ email +" already exists");
        }

        String encodedPassword = passwordEncoderPort.encode(command.password().trim());

        User userToSave = User.builder()
                .name(command.name())
                .email(command.email())
                .password(encodedPassword)
                .userStatus(UserStatus.ACTIVE)
                .role(UserRole.valueOf(command.role().toUpperCase()))
                .isEnabled(true)
                .isVerified(false)
                .lastLoginAt(null)
                .build();

        // 4. Persistence: Save via the Output Port
        User savedUser = userRepositoryPort.save(userToSave);

        return new RegisterUserUseCase.RegistrationResult(
                savedUser.getId(),
                savedUser.getEmail(),
                ""
        );
    }
}
