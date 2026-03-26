package edu.newnop.domain.model;

import edu.newnop.common.model.BaseDomainEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseDomainEntity implements UserDetails {
    private String name;
    private String email;
    private String password;
    private UserStatus userStatus;
    private UserRole role;
    private Instant lastLoginAt;
    private boolean isEnabled;
    private boolean isVerified;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !lastLoginAt.isBefore(Instant.now().minus(Duration.ofDays(30))) && !userStatus.equals(UserStatus.INACTIVE);
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }
}
