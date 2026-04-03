package edu.newnop.infrastructure.adapters.out.persistence;

import edu.newnop.domain.model.TaskPriority;
import edu.newnop.domain.model.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "tasks")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity extends TaskBaseJpaEntity {
    @Column(nullable = false)
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    @Column(name = "due_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;
    @Column(name = "assigned_user_id", nullable = false)
    private Long assignedUserId;
    @Column(name = "notification_sent", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean notificationSent;
}