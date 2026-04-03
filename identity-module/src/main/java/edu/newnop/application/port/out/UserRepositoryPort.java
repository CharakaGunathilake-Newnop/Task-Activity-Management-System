package edu.newnop.application.port.out;

import edu.newnop.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    void delete(User user);

    Optional<User> findById(Long userId);

    List<User> findAllByIdIn(Long[] userIds);
}
