package edu.newnop.infrastructure.adapters.out.persistence;

import edu.newnop.domain.model.TaskPriority;
import edu.newnop.domain.model.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity extends BaseJpaEntity {
    @Column(nullable = false)
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    @Column(name = "due_date")
    private Instant dueDate;
    @Column(name = "assigned_user_id", nullable = false)
    private Long assignedUserId;
}