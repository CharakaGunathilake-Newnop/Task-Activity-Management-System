package edu.newnop.infrastructure.adapters.out.persistence;

import edu.newnop.domain.dto.UserAnalyticsSummary;
import edu.newnop.domain.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    List<UserEntity> findAllByIdIn(Long[] userIds);

    boolean existsByName(String name);

    Page<UserEntity> findAllByRole(UserRole role, Pageable pageable);

    @Query("SELECT t FROM users t WHERE t.role = edu.newnop.domain.model.UserRole.USER AND " +
            "(LOWER(t.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(t.email) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(CAST(t.userStatus AS string)) LIKE LOWER(CONCAT('%', :searchQuery, '%')))")
    Page<UserEntity> findAllByRoleIsUserAndBySearch(@Param("searchQuery") String searchQuery, PageRequest pageRequest);

    @Query("SELECT new edu.newnop.domain.dto.UserAnalyticsSummary(" +
            "COUNT(u.id), " +
            "SUM(CASE WHEN u.isEnabled = true THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN u.isVerified = true THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN u.userStatus = edu.newnop.domain.model.UserStatus.ACTIVE THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN u.userStatus = edu.newnop.domain.model.UserStatus.INACTIVE THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN u.role = edu.newnop.domain.model.UserRole.ADMIN THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN u.role = edu.newnop.domain.model.UserRole.USER THEN 1L ELSE 0L END)) " +
            "FROM users u")
    UserAnalyticsSummary getUserAnalyticsSummary();
}
