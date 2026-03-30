package edu.newnop.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
   UserEntity findByEmail(String email);

    boolean existsByEmail(String email);
}
