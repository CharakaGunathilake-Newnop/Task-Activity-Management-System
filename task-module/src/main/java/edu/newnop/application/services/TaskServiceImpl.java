package edu.newnop.application.services;

import edu.newnop.application.out.TaskNotificationPort;
import edu.newnop.application.out.TaskRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepositoryPort taskRepository;
    private final TaskNotificationPort taskNotification;

    @Override
    public CreateTaskResult createTask(CreateTaskCommand command) {
        return null;
    }
}
