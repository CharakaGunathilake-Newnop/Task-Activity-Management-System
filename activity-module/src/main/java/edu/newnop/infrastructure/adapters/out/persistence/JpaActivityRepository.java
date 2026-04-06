package edu.newnop.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaActivityRepository extends JpaRepository<ActivityEntity, Long> {
}
