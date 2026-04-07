package edu.newnop.application.service;

import edu.newnop.application.port.in.*;
import edu.newnop.application.port.out.IdentityNotificationPort;
import edu.newnop.application.port.out.PasswordEncoderPort;
import edu.newnop.application.port.out.UserRepositoryPort;
import edu.newnop.application.port.out.dto.NotificationRequest;
import edu.newnop.common.event.EntityActivityEvent;
import edu.newnop.common.model.ActionType;
import edu.newnop.common.port.dto.UserResponseDto;
import edu.newnop.common.security.AuthenticatedUser;
import edu.newnop.domain.dto.UserAnalyticsSummary;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class IdentityServiceImpl implements IdentityService, IdentityAdminService {
    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final IdentityNotificationPort notificationPort;
    private final JwtService jwtService;
    private final AuthenticationEventPublisher authenticationEventPublisher;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ConcurrentHashMap<String, Map<String, Object>> otpStorage = new ConcurrentHashMap<>();

    private static final String OTP_KEY = "otpCode";
    private static final String ENTITY_NAME = "USERS";


    @Override
    public LoginResult login(LoginCommand command) {
        String email = command.email().toLowerCase().trim();
        String password = command.password().trim();

        User user = userRepositoryPort.findByEmail(command.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found by email: " + email));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        if (!user.isVerified()) {
            throw new EmailNotVerifiedException("Please verify your email before logging in.");
        }

        String token = jwtService.createToken(
                Map.of("userId", user.getId()),
                user.getUsername(),
                user.getRole().name()
        );

        user.setLastLoginAt(Instant.now());
        final User savedUser = userRepositoryPort.save(user);

        applicationEventPublisher.publishEvent(new EntityActivityEvent(
                ENTITY_NAME,
                savedUser.getId(),
                ActionType.LOGIN,
                String.format("%s logged in with email: %s at %tc", savedUser.getRole().name(), savedUser.getEmail(), ZonedDateTime.now()),
                user.getId()
        ));

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

        applicationEventPublisher.publishEvent(new EntityActivityEvent(
                ENTITY_NAME,
                savedUser.getId(),
                ActionType.REGISTER,
                String.format("%s registered with email: %s at %tc", savedUser.getRole().name(), savedUser.getEmail(), ZonedDateTime.now()),
                savedUser.getId()
        ));

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
        final String otpCode = otpStorage.get(otpId).get(OTP_KEY).toString();

        applicationEventPublisher.publishEvent(new EntityActivityEvent(
                ENTITY_NAME,
                user.getId(),
                ActionType.REQUEST_OTP,
                String.format("%s requested OTP with email: %s at %tc", user.getRole().name(), user.getEmail(), ZonedDateTime.now()),
                user.getId()
        ));

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
                , OTP_KEY, otpCode
                , "issuedAt", Instant.now()
        ));

        // 4. (Optional) Log for debugging - remove in production!
        log.debug("OTP for {} is [{}] with ID: {}", email, otpCode, otpId);

        return otpId;
    }

    @Override
    public SendMailResult sendVerificationMail(SendMailCommand<?> command) {

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

        if (validCode.get(OTP_KEY).equals(command.otpCode())) {
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

        applicationEventPublisher.publishEvent(new EntityActivityEvent(
                ENTITY_NAME,
                user.getId(),
                ActionType.VERIFY_EMAIL,
                String.format("%s verified their email: %s at %tc", user.getRole().name(), user.getEmail(), ZonedDateTime.now()),
                user.getId()
        ));

        userRepositoryPort.save(user);
    }

    @Override
    public GetProfileResult getProfile(GetProfileCommand command) {
        String email = command.email();

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by email: " + email));

        boolean isAuthenticated = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).isAuthenticated();

        log.info("Authenticate: {} -> {} requested their profile with email: {}",
                isAuthenticated,
                user.getRole().name(),
                command.email()
        );

        return new GetProfileResult(
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.isVerified(),
                user.getCreatedAt()
        );
    }

    @Override
    public UpdateProfileResult updateProfile(UpdateProfileCommand command) {
        String userId = command.userId();

        User user = userRepositoryPort.findByEmail(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by email: " + userId));

        // Update email if provided and different from current email
        if (null != command.email() && !command.email().trim().isEmpty()) {

            final String newEmail = command.email().trim();

            if (!user.getEmail().equalsIgnoreCase(newEmail)) {

                // Check if the new email is already taken by another user
                if (userRepositoryPort.existsByEmail(newEmail)) {
                    throw new UserAlreadyExistsException("Email is already in use by another account, please try a different email");
                }

                // If the email is being changed, we need to re-verify the new email
                user.setVerified(false);
                user.setEmail(newEmail);

                // Generate and send OTP to the new email for verification
                String otpId = generateAndStoreOtp(newEmail);
                sendVerificationMail(
                        new SendMailCommand<>(
                                newEmail,
                                user.getName().split(" ")[0],
                                "Email Change Verification",
                                "Your email change request is received. Please verify your new email using the OTP code provided here",
                                Map.of("OTP code", otpStorage.get(otpId).get(OTP_KEY).toString())
                        )
                );
            }
        }

        // Update name if provided
        if (null != command.name() && !command.name().isEmpty()) user.setName(command.name());

        User updatedUser = userRepositoryPort.save(user);

        applicationEventPublisher.publishEvent(new EntityActivityEvent(
                ENTITY_NAME,
                updatedUser.getId(),
                ActionType.UPDATE,
                String.format("%s updated their profile at %tc", user.getRole().name(), ZonedDateTime.now()),
                user.getId()
        ));

        return new UpdateProfileResult(
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getRole().name(),
                updatedUser.isVerified(),
                updatedUser.getCreatedAt()
        );
    }

    @Override
    public ChangePasswordResult changePassword(ChangePasswordCommand command) {
        String email = command.email();

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by email: " + email));

        if (!passwordEncoderPort.matches(command.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect, please try again");
        }

        String encodedNewPassword = passwordEncoderPort.encode(command.newPassword());
        user.setPassword(encodedNewPassword);
        User savedUser = userRepositoryPort.save(user);

        applicationEventPublisher.publishEvent(new EntityActivityEvent(
                ENTITY_NAME,
                savedUser.getId(),
                ActionType.CHANGE_PASSWORD,
                String.format("%s changed their password with email: %s at %tc", savedUser.getRole().name(), savedUser.getEmail(), ZonedDateTime.now()),
                user.getId()
        ));

        return new ChangePasswordResult("Password changed successfully");
    }

    @Override
    public DeleteAccountResult deleteAccount(DeleteAccountCommand command) {
        String email = command.email();

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by email: " + email));

        if (!passwordEncoderPort.matches(command.password(), user.getPassword())) {
            throw new IllegalArgumentException("Password is incorrect, please try again");
        }

        applicationEventPublisher.publishEvent(new EntityActivityEvent(
                ENTITY_NAME,
                user.getId(),
                ActionType.DELETE,
                String.format("%s deleted their account with email: %s at %tc", user.getRole().name(), user.getEmail(), ZonedDateTime.now()),
                user.getId()
        ));

        userRepositoryPort.delete(user);

        return new DeleteAccountResult("Account deleted successfully");
    }

    @Override
    public GetAllUsersResult getAllUsers(GetTotalUserCountCommand command) {
        final AuthenticatedUser user = getUser();
        // Validate and sanitize pagination parameters
        final Pageable pageable = command.pageRequest();
        final PageRequest pageRequest = PageRequest.of(
                Math.max(0, pageable.getPageNumber()), // Ensure page number is non-negative
                pageable.getPageSize() < 0 ? 10 : Math.min(pageable.getPageSize(), 100), // Default page size to 10 and cap at 100
                pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(Sort.Direction.DESC, "createdAt") // Default sorting by createdAt desc
        );

        final String searchQuery = command.searchQuery() != null ? command.searchQuery().trim() : "";

        Page<User> users;

        if (searchQuery.isEmpty()) {
            users = userRepositoryPort.findAllUsersByRole(UserRole.USER, pageRequest);
        } else {
            users = userRepositoryPort.findAllUsersBySearch(searchQuery, pageRequest);
        }

        log.info("ADMIN: ID [{}] with email: {} requested users page: [{}] {} of all users",
                user.userId(),
                user.email(),
                pageable.getPageNumber(),
                searchQuery.isEmpty() ? "" : "search by [" + searchQuery + "]"
        );

        List<UserResponseDto> userResponseList = users.getContent().stream().map(u ->
                UserResponseDto.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .userStatus(u.getUserStatus().name())
                        .isEnabled(u.isEnabled())
                        .isVerified(u.isVerified())
                        .lastLoginAt(u.getLastLoginAt())
                        .createdAt(u.getCreatedAt())
                        .lastUpdatedAt(u.getLastUpdate())
                        .build()
        ).toList();

        return new GetAllUsersResult(
                userResponseList,
                users.getNumber() == 0 ? 1 : users.getNumber() + 1, // Convert to 1-based page number for client
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages()
        );
    }

    @Override
    public DeactivateUserResult deactivateUser(Long userId) {
        User user = userRepositoryPort.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found by ID: " + userId));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("User is already deactivated");
        }

        user.setEnabled(false);
        user.setUserStatus(UserStatus.INACTIVE);

        User savedUser = userRepositoryPort.save(user);


        applicationEventPublisher.publishEvent(new EntityActivityEvent(
                ENTITY_NAME,
                user.getId(),
                ActionType.DEACTIVATE,
                String.format("ADMIN: ID[%s] deactivated the USER account with email: %s at %tc", getUser().userId(), savedUser.getEmail(), ZonedDateTime.now()),
                user.getId()
        ));

        // Notify user
        notificationPort.sendMail(
                new NotificationRequest<>(
                        user.getEmail(),
                        user.getName().split(" ")[0],
                        "Account deactivated by Admin",
                        "Your account has been deactivated by Admin, please contact support",
                        null
                )
        );


        return new DeactivateUserResult(
                UserResponseDto.builder()
                        .id(savedUser.getId())
                        .name(savedUser.getName())
                        .email(savedUser.getEmail())
                        .userStatus(savedUser.getUserStatus().name())
                        .isEnabled(savedUser.isEnabled())
                        .isVerified(savedUser.isVerified())
                        .lastLoginAt(savedUser.getLastLoginAt())
                        .createdAt(savedUser.getCreatedAt())
                        .lastUpdatedAt(savedUser.getLastUpdate())
                        .build()
        );
    }

    @Override
    public void deleteUser(Long userId) {

        final User user = userRepositoryPort.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found by ID: " + userId));

        userRepositoryPort.delete(user);

        applicationEventPublisher.publishEvent(new EntityActivityEvent(
                ENTITY_NAME,
                user.getId(),
                ActionType.DELETE,
                String.format("ADMIN: ID[%s] deleted the USER account with email: %s at %tc", getUser().userId(), user.getEmail(), ZonedDateTime.now()),
                user.getId()
        ));

        // Notify user
        notificationPort.sendMail(
                new NotificationRequest<>(
                        user.getEmail(),
                        user.getName().split(" ")[0],
                        "Account deactivated by Admin",
                        "Your account has been deleted by Admin, please contact support",
                        null
                )
        );
    }

    @Override
    public Map<String, Long> getUserBreakdown() {
        final AuthenticatedUser user = getUser();
        log.info("ADMIN ID [{}] with email: {} requested total user breakdown",
                user.userId(),
                user.email()
        );

        UserAnalyticsSummary userAnalyticsSummary = userRepositoryPort.getUserBreakDown();

        return Map.of(
                "totalUsers", userAnalyticsSummary.totalUsers(),
                "enabledUsers", userAnalyticsSummary.enabledUsers(),
                "verifiedUsers", userAnalyticsSummary.verifiedUsers(),
                "activeUsers", userAnalyticsSummary.activeUsers(),
                "inactiveUsers", userAnalyticsSummary.inactiveUsers(),
                "adminUsers", userAnalyticsSummary.adminUsers(),
                "generalUsers", userAnalyticsSummary.generalUsers()
        );
    }

    private AuthenticatedUser getUser() {
        try {
            return (AuthenticatedUser) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        } catch (NullPointerException e) {
            throw new UsernameNotFoundException("No authenticated user found in security context");
        }
    }
}
