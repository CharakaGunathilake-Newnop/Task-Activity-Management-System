package edu.newnop.infrastructure.adapters.out.persistence;

import edu.newnop.domain.dto.TaskAnalyticsSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
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

    // Custom query to search with pagination
    @Query("SELECT t FROM tasks t WHERE " +
            "LOWER(t.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(t.status) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(t.priority) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
    Page<TaskEntity> findAllWithSearchQuery(String searchQuery, PageRequest pageRequest);

    List<TaskEntity> findAllByDueDateIsBeforeAndNotificationSentFalse(Date threshold);

    @Query("""
        SELECT new edu.newnop.domain.dto.TaskAnalyticsSummary(
            COUNT(t.id),
            SUM(CASE WHEN t.status = edu.newnop.domain.model.TaskStatus.TODO THEN 1L ELSE 0L END),
            SUM(CASE WHEN t.status = edu.newnop.domain.model.TaskStatus.IN_PROGRESS THEN 1L ELSE 0L END),
            SUM(CASE WHEN t.status = edu.newnop.domain.model.TaskStatus.DONE THEN 1L ELSE 0L END),
            SUM(CASE WHEN t.priority = edu.newnop.domain.model.TaskPriority.LOW THEN 1L ELSE 0L END),
            SUM(CASE WHEN t.priority = edu.newnop.domain.model.TaskPriority.MEDIUM THEN 1L ELSE 0L END),
            SUM(CASE WHEN t.priority = edu.newnop.domain.model.TaskPriority.HIGH THEN 1L ELSE 0L END),
            SUM(CASE WHEN t.dueDate >= CURRENT_TIMESTAMP THEN 1L ELSE 0L END),
            SUM(CASE WHEN t.dueDate < CURRENT_TIMESTAMP AND t.status != edu.newnop.domain.model.TaskStatus.DONE THEN 1L ELSE 0L END)
        )
        FROM tasks t
    """)
    TaskAnalyticsSummary getGlobalTaskAnalyticsSummary();
}
