package edu.newnop.application.service;

import edu.newnop.application.port.in.*;
import edu.newnop.application.port.out.IdentityNotificationPort;
import edu.newnop.application.port.out.PasswordEncoderPort;
import edu.newnop.application.port.out.UserRepositoryPort;
import edu.newnop.application.port.out.dto.NotificationRequest;
import edu.newnop.domain.model.User;
import edu.newnop.domain.model.UserRole;
import edu.newnop.domain.model.UserStatus;
import edu.newnop.infrastructure.adapters.in.web.exceptions.EmailNotVerifiedException;
import edu.newnop.infrastructure.adapters.in.web.exceptions.InvalidUserRoleException;
import edu.newnop.infrastructure.adapters.in.web.exceptions.OtpVerificationFailedException;
import edu.newnop.infrastructure.adapters.in.web.exceptions.UserAlreadyExistsException;
import edu.newnop.infrastructure.adapters.out.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class IdentityService implements RegisterUserUseCase, LoginUseCase, RequestOTPMailUseCase, ProfileManagementUseCase, DeleteAccountUseCase {
    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final IdentityNotificationPort notificationPort;
    private final JwtService jwtService;
    private final ConcurrentHashMap<String, Map<String, Object>> otpStorage = new ConcurrentHashMap<>();


    @Override
    public LoginResult login(LoginCommand command) {
        String email = command.email().toLowerCase().trim();
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
    public RegistrationResult<RequestOTPMailResult> register(RegisterCommand command) {
        String email = command.email().trim();
        String role = command.role().trim();

        if (!role.equalsIgnoreCase("user") && !role.equalsIgnoreCase("admin")) {
            throw new InvalidUserRoleException("Not a valid user role provided please try User or Admin");
        }

        if (userRepositoryPort.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with Email:" + email + " already exists");
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

        User savedUser = userRepositoryPort.save(userToSave);

        return new RegisterUserUseCase.RegistrationResult<>(
                savedUser.getId(),
                savedUser.getEmail(),
                generateAndSendVerificationMail(new RequestOTPMailCommand(
                        email
                ))
        );
    }

    @Override
    public RequestOTPMailResult generateAndSendVerificationMail(RequestOTPMailCommand command) {
        String email = command.email().toLowerCase().trim();

        User user = userRepositoryPort.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found by email: " + email)
        );

        final String otpId = generateAndStoreOtp(command.email());
        final String otpCode = otpStorage.get(otpId).get("otpCode").toString();
        return new RequestOTPMailResult(
                otpId,
                sendVerificationMail(
                        new SendMailCommand<>(
                                command.email(),
                                user.getName().split(" ")[0],
                                "Sign Up Successful",
                                "Your account is created successfully as an " + user.getRole() +
                                        ".\n please verify your mail using the otp code provided here",
                                Map.of("OTP code", otpCode)
                        )
                ).message()
        );
    }

    /**
     * Generates an OTP, stores it, and returns the unique OtpID.
     */
    private String generateAndStoreOtp(String email) {
        // 1. Generate a unique ID for this specific OTP attempt
        String otpId = UUID.randomUUID().toString();

        // 2. Generate a random 6-digit code
        String otpCode = String.format("%06d", new Random().nextInt(999999));

        // 3. Store in the map
        // Note: In a real app, you'd associate this with the user's email too.
        otpStorage.put(otpId, Map.of(
                "email", email
                , "otpCode", otpCode
                , "issuedAt", Instant.now()
        ));

        // 4. (Optional) Log for debugging - remove in production!
        log.debug("OTP for {} is [{}] with ID: {}", email, otpCode, otpId);

        return otpId;
    }

    @Override
    public SendMailResult sendVerificationMail(SendMailCommand<?> command) {

        log.debug("Sending mail");
        return new SendMailResult(
                notificationPort.sendMail(
                        NotificationRequest.builder()
                                .receiverName(command.receiverName())
                                .receiverEmail(command.receiverEmail())
                                .subject(command.subject())
                                .message(command.message())
                                .data(command.data())
                                .build()
                )

        );
    }

    @Override
    public boolean verifyEmail(VerifyEmailCommand command) {
        final Map<String, Object> validCode = otpStorage.get(command.otpId());

        if (null == validCode) throw new OtpVerificationFailedException("Invalid OTP");

        final String email = validCode.get("email").toString();
        final Instant issuedAt = Instant.from((Instant) validCode.get("issuedAt"));
        final boolean isExpired = issuedAt.isBefore(Instant.now().minus(Duration.ofMinutes(15)));


        if (isExpired) {
            otpStorage.remove(command.otpId());
            throw new OtpVerificationFailedException("Failed to verify your email due to Expired OTP provided");
        }

        if (validCode.get("otpCode").equals(command.otpCode())) {
            updateUserVerificationStatus(email);
            otpStorage.remove(command.otpId());
            return true;
        }

        throw new OtpVerificationFailedException("Failed to verify your email, please try again");
    }

    private void updateUserVerificationStatus(String email) {
        User user = userRepositoryPort.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Email verification failed due to user not found")
        );

        log.debug("User email is verified via OTP");
        user.setVerified(true);

        userRepositoryPort.save(user);
    }

    @Override
    public GetProfileResult getProfile(GetProfileCommand command) {
        return null;
    }

    @Override
    public UpdateProfileResult updateProfile(UpdateProfileCommand command) {
        return null;
    }

    @Override
    public ChangePasswordResult changePassword(ChangePasswordCommand command) {
        return null;
    }

    @Override
    public DeleteAccountResult deleteAccount(DeleteAccountCommand command) {
        return null;
    }
}
