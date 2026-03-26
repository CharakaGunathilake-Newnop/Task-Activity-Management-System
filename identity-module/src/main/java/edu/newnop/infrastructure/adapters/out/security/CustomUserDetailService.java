package edu.newnop.infrastructure.adapters.out.security;

import edu.newnop.infrastructure.adapters.out.persistence.JpaUserRepository;
import edu.newnop.infrastructure.adapters.out.persistence.UserEntity;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final JpaUserRepository userRepository;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found b y email: " + email)
        );
        return new User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
