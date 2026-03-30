package edu.newnop.application.in;

import java.util.Date;

public interface CreateTaskUseCase {
    CreateTaskResult createTask(CreateTaskCommand command);

    record CreateTaskCommand(
            String title,
            String description,
            String status,
            String priority,
            Date dueDate,
            String assignedUserId
    ) {
    }

    record CreateTaskResult(
            Long id,
            String title,
            String description,
            String status,
            String priority,
            Date dueDate
    ) {
    }
}
