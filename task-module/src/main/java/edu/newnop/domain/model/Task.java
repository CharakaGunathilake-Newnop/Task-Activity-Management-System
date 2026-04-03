package edu.newnop.domain.model;

import edu.newnop.common.model.BaseDomainEntity;
import lombok.*;

import java.util.Date;

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
    private Date dueDate;
    private Long assignedUserId;
    private boolean notificationSent;
}
