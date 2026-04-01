package edu.newnop.infrastructure.adapters.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaTaskRepository extends JpaRepository<TaskEntity, Long> {
    Page<TaskEntity> findAllByAssignedUserId(Long assignedUserId, PageRequest pageRequest);

    Optional<TaskEntity> findByIdAndAssignedUserId(Long taskId, Long assignedUserId);

    // Custom query to search by title, status, or priority for a specific user with pagination
    @Query("SELECT t FROM tasks t WHERE t.assignedUserId = :assignedUserId AND " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(t.status) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(t.priority) LIKE LOWER(CONCAT('%', :searchQuery, '%')))")
    Page<TaskEntity> findByAssignedUserIdAndSearchQuery(Long assignedUserId, String searchQuery, PageRequest pageRequest);

    // Custom query to search by title, status, or priority for all tasks with pagination
    @Query("SELECT t FROM tasks t WHERE " +
            "LOWER(t.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(t.status) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(t.priority) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
    Page<TaskEntity> findAllWithSearchQuery(String searchQuery, PageRequest pageRequest);
}
