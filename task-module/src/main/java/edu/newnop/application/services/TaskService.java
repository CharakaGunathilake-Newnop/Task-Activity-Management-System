package edu.newnop.application.services;

import edu.newnop.application.in.CreateTaskUseCase;
import edu.newnop.application.in.DeleteTaskUseCase;
import edu.newnop.application.in.GetTasksUseCase;
import edu.newnop.application.in.UpdateTaskUseCase;

public interface TaskService extends
        CreateTaskUseCase,
        GetTasksUseCase,
        UpdateTaskUseCase,
        DeleteTaskUseCase {
}
