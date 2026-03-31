package edu.newnop.application.in;

public interface DeleteTaskUseCase {
    DeleteTaskResult deleteTask(DeleteTaskCommand command);

    record DeleteTaskCommand(
            Long taskId
    ) {
    }

    record DeleteTaskResult(
            String message
    ) {
    }
}
