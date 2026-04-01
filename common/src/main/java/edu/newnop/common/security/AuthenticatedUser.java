package edu.newnop.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public record AuthenticatedUser(
        Long userId,
        String name,
        String email,
        String password,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {
    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return password; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
}
