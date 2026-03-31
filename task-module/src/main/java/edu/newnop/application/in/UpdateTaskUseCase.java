package edu.newnop.application.in;

import java.util.Date;

public interface UpdateTaskUseCase {
    UpdateTaskResult updateTask(UpdateTaskCommand command);

    record UpdateTaskCommand(
            Long taskId,
            String title,
            String description,
            String status,
            String priority,
            Date dueDate
    ) {
    }

    record UpdateTaskResult(
            Long taskId,
            String title,
            String description,
            String status,
            String priority,
            Date dueDate
    ) {
    }

    UpdateTaskStatusResult updateTaskStatus(UpdateTaskStatusCommand command);

    record UpdateTaskStatusCommand(
            Long taskId,
            String status
    ) {
    }

    record UpdateTaskStatusResult(
            Long taskId,
            String status
    ) {
    }

}
