package edu.newnop.infrastructure.adapters.out.security;

import edu.newnop.common.security.AuthenticatedUser;
import edu.newnop.domain.model.User;
import edu.newnop.infrastructure.adapters.out.persistence.PostgresUserAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final PostgresUserAdapter userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Load user and return from database using email
        final User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found by email: " + email)
        );

        return new AuthenticatedUser(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAuthorities()
        );
    }
}
