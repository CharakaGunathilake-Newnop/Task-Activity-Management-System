package edu.newnop.application.services;

import edu.newnop.application.out.TaskNotificationPort;
import edu.newnop.application.out.TaskRepositoryPort;
import edu.newnop.application.out.dto.NotificationRequest;
import edu.newnop.common.security.AuthenticatedUser;
import edu.newnop.domain.model.Task;
import edu.newnop.domain.model.TaskPriority;
import edu.newnop.domain.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepositoryPort taskRepository;
    private final TaskNotificationPort taskNotification;

    @Override
    public CreateTaskResult createTask(CreateTaskCommand command) {
        TaskStatus status = TaskStatus.TODO;
        TaskPriority priority = TaskPriority.MEDIUM;

        if (null != command.status() && !command.status().isBlank()) {
            final TaskStatus[] values = TaskStatus.values();

            if (Arrays.stream(values).noneMatch(s -> s.name().equalsIgnoreCase(command.status()))) {
                throw new IllegalArgumentException("Invalid task status: " + command.status());
            }

            status = TaskStatus.valueOf(command.status().toUpperCase());
        }

        if (null != command.priority() && !command.priority().isBlank()) {
            final TaskPriority[] priorityValues = TaskPriority.values();

            if (Arrays.stream(priorityValues).noneMatch(p -> p.name().equalsIgnoreCase(command.priority()))) {
                throw new IllegalArgumentException("Invalid task priority: " + command.priority());
            }

            priority = TaskPriority.valueOf(command.priority().toUpperCase());
        }

        AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Long userId = user.userId();

        // Create task in database
        Task task = taskRepository.save(new Task(
                command.title(),
                command.description(),
                status,
                priority,
                command.dueDate() != null ? command.dueDate() : null,
                userId
        ));

        taskNotification.notifyTaskCreated(
                new NotificationRequest<>(
                        user.email(),
                        user.name().split(" ")[0],
                        String.format("Task '%s' created successfully", task.getTitle()),
                        "You have created a new task with the following details",
                        new CreateTaskResult(
                                task.getId(),
                                task.getTitle(),
                                task.getDescription(),
                                task.getStatus().name(),
                                task.getPriority().name(),
                                task.getDueDate()
                        )
                )
        );

        return new CreateTaskResult(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getPriority().name(),
                task.getDueDate()
        );

    }
}
