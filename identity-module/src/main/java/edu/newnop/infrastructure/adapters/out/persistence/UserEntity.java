package edu.newnop.infrastructure.adapters.out.persistence;

import edu.newnop.domain.model.UserRole;
import edu.newnop.domain.model.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@EqualsAndHashCode(callSuper=false)
@Data
@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseJPAEntity {
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Column(name = "last_login_at")
    private Instant lastLoginInAt;
    @Column(name = "is_enabled")
    private boolean isEnabled;
    @Column(name = "is_verified")
    private boolean isVerified;
}
