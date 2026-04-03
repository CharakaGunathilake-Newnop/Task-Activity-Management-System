package edu.newnop.application.services;

import edu.newnop.application.out.TaskNotificationPort;
import edu.newnop.application.out.TaskRepositoryPort;
import edu.newnop.application.out.dto.NotificationRequest;
import edu.newnop.common.security.AuthenticatedUser;
import edu.newnop.domain.model.Task;
import edu.newnop.domain.model.TaskPriority;
import edu.newnop.domain.model.TaskStatus;
import edu.newnop.infrastructure.adapters.in.web.dto.TaskResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;


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

        AuthenticatedUser user = getUser();

        // Create task in database
        Task task = taskRepository.save(new Task(
                command.title(),
                command.description(),
                status,
                priority,
                command.dueDate() != null ? command.dueDate() : null,
                user.userId(),
                false
        ));

        final CreateTaskResult createTaskResult = new CreateTaskResult(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getPriority().name(),
                task.getDueDate()
        );

        taskNotification.notifyTaskEvent(
                new NotificationRequest<>(
                        user.email(),
                        user.name().split(" ")[0],
                        String.format("Task '%s' created successfully", task.getTitle()),
                        "You have created a new task with the following details",
                        createTaskResult
                )
        );

        return createTaskResult;

    }

    @Override
    public GetTasksResult getPaginatedTasks(GetTasksCommand command) {
        // Validate and sanitize pagination parameters
        Pageable pageable = command.pageRequest();
        PageRequest pageRequest = PageRequest.of(
                Math.max(0, pageable.getPageNumber()), // Ensure page number is non-negative
                pageable.getPageSize() < 0 ? 10 : Math.min(pageable.getPageSize(), 100), // Default page size to 10 and cap at 100
                pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(Sort.Direction.DESC, "createdAt") // Default sorting by createdAt desc
        );

        final String searchQuery = command.searchQuery() != null ? command.searchQuery().trim() : "";

        final Long selectedUserId = command.selectedUserId();

        final AuthenticatedUser user = getUser();

        final List<String> userRoles = user.authorities().stream().map(GrantedAuthority::getAuthority).toList();

        final Long userId = userRoles.contains("ROLE_ADMIN") ? selectedUserId : user.userId(); // Admin can see all tasks, regular users only their own

        Page<Task> tasks;

        // If user is admin and no specific userId is provided, return all tasks with search query
        if (userRoles.contains("ROLE_ADMIN") && null == selectedUserId) {
            tasks = searchQuery.isEmpty() ? taskRepository.findAll(pageRequest) : taskRepository.findAllWithSearchQuery(searchQuery, pageRequest);
        } else if (searchQuery.isEmpty()) {
            tasks = taskRepository.findAllByUserId(userId, pageRequest);
        } else {
            // Implement a custom search query with filtering by title, status and priority
            tasks = taskRepository.findByUserIdAndSearchQuery(userId, searchQuery, pageRequest);
        }

        List<TaskResponseDto> tasksList = tasks.getContent().stream().map(task ->
                TaskResponseDto.builder()
                        .id(task.getId())
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .status(task.getStatus())
                        .priority(task.getPriority())
                        .dueDate(task.getDueDate())
                        .assignedUserId(task.getAssignedUserId())
                        .build()
        ).toList();

        return new GetTasksResult(
                tasksList,
                tasks.getNumber() == 0 ? 1 : tasks.getNumber() + 1, // Convert to 1-based page number for client
                tasks.getSize(),
                tasks.getTotalElements(),
                tasks.getTotalPages()
        );
    }

    @Override
    public UpdateTaskResult updateTask(UpdateTaskCommand command) {
        AuthenticatedUser user = getUser();
        Task task = taskRepository.findByIdAndUserId(command.taskId(), user.userId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + command.taskId()));

        if (null != command.title() && !command.title().isBlank()) {
            task.setTitle(command.title());
        }

        if (null != command.description() && !command.description().isBlank()) {
            task.setDescription(command.description());
        }

        if (null != command.status() && !command.status().isBlank()) {
            final TaskStatus[] values = TaskStatus.values();

            if (Arrays.stream(values).noneMatch(s -> s.name().equalsIgnoreCase(command.status()))) {
                throw new IllegalArgumentException("Invalid task status: " + command.status());
            }

            task.setStatus(TaskStatus.valueOf(command.status().toUpperCase()));
        }

        if (null != command.priority() && !command.priority().isBlank()) {
            final TaskPriority[] priorityValues = TaskPriority.values();

            if (Arrays.stream(priorityValues).noneMatch(p -> p.name().equalsIgnoreCase(command.priority()))) {
                throw new IllegalArgumentException("Invalid task priority: " + command.priority());
            }

            task.setPriority(TaskPriority.valueOf(command.priority().toUpperCase()));
        }

        if (null != command.dueDate()) {
            task.setDueDate(command.dueDate());
        }

        Task updatedTask = taskRepository.save(task);

        final UpdateTaskResult updateTaskResult = new UpdateTaskResult(
                updatedTask.getId(),
                updatedTask.getTitle(),
                updatedTask.getDescription(),
                updatedTask.getStatus().name(),
                updatedTask.getPriority().name(),
                updatedTask.getDueDate()
        );

        // Notify user about task update
        taskNotification.notifyTaskEvent(
                new NotificationRequest<>(
                        user.email(),
                        user.name().split(" ")[0],
                        String.format("Task '%s' updated successfully", updatedTask.getTitle()),
                        "You have updated a task with the following details",
                        updateTaskResult
                )
        );

        return updateTaskResult;
    }

    @Override
    public UpdateTaskStatusResult updateTaskStatus(UpdateTaskStatusCommand command) {
        AuthenticatedUser user = getUser();
        Task task = taskRepository.findByIdAndUserId(command.taskId(), user.userId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + command.taskId()));

        if (null != command.status() && !command.status().isBlank()) {
            final TaskStatus[] values = TaskStatus.values();

            if (Arrays.stream(values).noneMatch(s -> s.name().equalsIgnoreCase(command.status()))) {
                throw new IllegalArgumentException("Invalid task status: " + command.status());
            }

            task.setStatus(TaskStatus.valueOf(command.status().toUpperCase()));
        }

        Task updatedTask = taskRepository.save(task);

        final UpdateTaskStatusResult updateTaskStatusResult = new UpdateTaskStatusResult(
                updatedTask.getId(),
                updatedTask.getStatus().name()
        );

        // Notify user about task status update
        taskNotification.notifyTaskEvent(
                new NotificationRequest<>(
                        user.email(),
                        user.name().split(" ")[0],
                        String.format("Task '%s' status updated to '%s'", updatedTask.getTitle(), updatedTask.getStatus().name()),
                        "You have updated the status of a task with the following details",
                        updateTaskStatusResult
                )
        );

        return updateTaskStatusResult;
    }

    @Override
    public DeleteTaskResult deleteTask(DeleteTaskCommand command) {
        AuthenticatedUser user = getUser();
        Task task = taskRepository.findByIdAndUserId(command.taskId(), user.userId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + command.taskId()));

        taskRepository.delete(task);

        final DeleteTaskResult deleteTaskResult = new DeleteTaskResult(
                task.getTitle()
        );

        // Notify user about task deletion
        taskNotification.notifyTaskEvent(
                new NotificationRequest<>(
                        user.email(),
                        user.name().split(" ")[0],
                        String.format("Task '%s' deleted successfully", task.getTitle()),
                        "You have deleted a task with the following details",
                        deleteTaskResult
                )
        );

        return deleteTaskResult;
    }

    private AuthenticatedUser getUser() {
        try {
            return (AuthenticatedUser) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        } catch (NullPointerException e) {
            throw new UsernameNotFoundException("No authenticated user found in security context");
        }
    }
}
