package edu.newnop.domain.model;

import edu.newnop.common.model.BaseDomainEntity;
import lombok.*;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task extends BaseDomainEntity {
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Instant dueDate;
    private Long assignedUserId;
}
