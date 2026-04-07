package edu.newnop.infrastructure.adapters.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaActivityRepository extends JpaRepository<ActivityEntity, Long> {
    // Custom query to search by entityName or actionType for a specific user with pagination
    @Query("SELECT a FROM activity_log a WHERE " +
            "LOWER(a.entityName) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(a.actionType) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
    Page<ActivityEntity> findAllWithSearchQuery(String searchQuery, PageRequest pageRequest);

    Page<ActivityEntity> findAllByActorId(Long actorId, PageRequest pageRequest);

    @Query("SELECT a FROM activity_log a WHERE a.actorId = :actorId AND " +
            "(LOWER(a.entityName) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(a.actionType) LIKE LOWER(CONCAT('%', :searchQuery, '%')))")
    Page<ActivityEntity> findByUserIdAndSearchQuery(Long actorId, String searchQuery, PageRequest pageRequest);
}
