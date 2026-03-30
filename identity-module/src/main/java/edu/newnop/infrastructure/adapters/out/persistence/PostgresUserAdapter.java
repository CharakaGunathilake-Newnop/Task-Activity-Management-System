package edu.newnop.infrastructure.adapters.out.persistence;

import edu.newnop.application.port.out.UserRepositoryPort;
import edu.newnop.domain.model.User;
import edu.newnop.infrastructure.adapters.out.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostgresUserAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaRepository;

    @Override
    public User save(User user) {
        // 1. Map Domain -> Entity
        UserEntity entity = UserMapper.toEntity(user);

        // 2. Map Entity -> Domain and return
        return UserMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<User> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        UserEntity entity = jpaRepository.findByEmail(email).orElse(null);
        return Optional.ofNullable(UserMapper.toDomain(entity));

    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public void delete(User user) {
        jpaRepository.delete(UserMapper.toEntity(user));
    }
}
