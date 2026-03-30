package edu.newnop.infrastructure.adapters.out.mapper;

import edu.newnop.domain.model.Task;
import edu.newnop.infrastructure.adapters.out.persistence.TaskEntity;

public class TaskMapper {
    private TaskMapper() {
        // Private constructor to prevent instantiation
    }

    public static TaskEntity toEntity(Task task) {
        if (task == null) {
            return null;
        }
        TaskEntity entity = new TaskEntity();
        entity.setId(task.getId());
        entity.setVersion(task.getVersion());
        entity.setTitle(task.getTitle());
        entity.setDescription(task.getDescription() != null ? task.getDescription() : null);
        entity.setStatus(task.getStatus() != null ? task.getStatus() : null);
        entity.setCreatedAt(task.getCreatedAt());
        entity.setLastUpdate(task.getLastUpdate());
        entity.setDueDate(task.getDueDate() != null ? task.getDueDate() : null);
        entity.setAssignedUserId(task.getAssignedUserId());

        return entity;
    }

    public static Task toDomain(TaskEntity entity) {
        if (entity == null) {
            return null;
        }
        Task task = Task.builder()
                .title(entity.getTitle())
                .description(entity.getDescription() != null ? entity.getDescription() : null)
                .status(entity.getStatus() != null ? entity.getStatus() : null)
                .priority(entity.getPriority() != null ? entity.getPriority() : null)
                .dueDate(entity.getDueDate() != null ? entity.getDueDate() : null)
                .assignedUserId(entity.getAssignedUserId())
                .build();

        task.setId(entity.getId());
        task.setVersion(entity.getVersion());
        task.setCreatedAt(entity.getCreatedAt());
        task.setLastUpdate(entity.getLastUpdate());

        return task;
    }
}
