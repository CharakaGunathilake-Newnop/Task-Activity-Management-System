package edu.newnop.infrastructure.adapters.in.web.dto;

import edu.newnop.domain.model.TaskPriority;
import edu.newnop.domain.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Date dueDate;
    private Long assignedUserId;

    public TaskResponseDto fromDomain(edu.newnop.domain.model.Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .assignedUserId(task.getAssignedUserId())
                .build();
    }
}
