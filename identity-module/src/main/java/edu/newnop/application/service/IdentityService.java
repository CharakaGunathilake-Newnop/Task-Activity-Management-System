package edu.newnop.application.service;

import edu.newnop.application.port.in.LoginUseCase;
import edu.newnop.application.port.in.RegisterUserUseCase;
import edu.newnop.application.port.out.PasswordEncoderPort;
import edu.newnop.application.port.out.UserRepositoryPort;
import edu.newnop.domain.model.User;
import edu.newnop.domain.model.UserRole;
import edu.newnop.domain.model.UserStatus;
import edu.newnop.infrastructure.adapters.in.web.exceptions.EmailNotVerifiedException;
import edu.newnop.infrastructure.adapters.in.web.exceptions.InvalidUserRoleException;
import edu.newnop.infrastructure.adapters.in.web.exceptions.UserAlreadyExistsException;
import edu.newnop.infrastructure.adapters.out.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class IdentityService implements RegisterUserUseCase, LoginUseCase {
    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtService jwtService;


    @Override
    public LoginResult login(LoginCommand command) {
        String email = command.email().trim();
        String password = command.password().trim();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        User user = userRepositoryPort.findByEmail(command.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found by email: " + email));

        if (!user.isVerified()) {
            throw new EmailNotVerifiedException("Please verify your email before logging in.");
        }

        String token = jwtService.createToken(
                Map.of("userId", user.getId()),
                user.getUsername(),
                user.getRole().name()
        );

        user.setLastLoginAt(Instant.now());
        userRepositoryPort.save(user);

        return new LoginResult(
                token,
                user.getUsername(),
                user.getRole().name()
        );
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
