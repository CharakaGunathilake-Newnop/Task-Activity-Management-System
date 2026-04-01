package edu.newnop.infrastructure.adapters.in.web;


import edu.newnop.application.in.CreateTaskUseCase;
import edu.newnop.application.in.DeleteTaskUseCase;
import edu.newnop.application.in.GetTasksUseCase;
import edu.newnop.application.in.UpdateTaskUseCase;
import edu.newnop.common.ApiResponse;
import edu.newnop.infrastructure.adapters.in.web.dto.CreateTaskRequest;
import edu.newnop.infrastructure.adapters.in.web.dto.UpdateTaskRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {
    private final CreateTaskUseCase createTaskUseCase;
    private final GetTasksUseCase getTasksUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<CreateTaskUseCase.CreateTaskResult> createTask(@RequestBody @Valid CreateTaskRequest request) {
        return ApiResponse.success(
                201,
                String.format("Task '%s' created successfully", request.getTitle()),
                createTaskUseCase.createTask(
                        new CreateTaskUseCase.CreateTaskCommand(
                                request.getTitle(),
                                request.getDescription(),
                                request.getStatus(),
                                request.getPriority(),
                                request.getDueDate()
                        )
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<GetTasksUseCase.GetTasksResult> getTasks(
            @PageableDefault(page = 1, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long selectedUserId) {
        return ApiResponse.success(
                200,
                "Tasks retrieved successfully",
                getTasksUseCase.getPaginatedTasks(
                        new GetTasksUseCase.GetTasksCommand(
                                PageRequest.of(
                                        Math.max(0, pageable.getPageNumber() - 1),
                                        pageable.getPageSize(),
                                        pageable.getSort()
                                ),
                                search,
                                selectedUserId
                        )
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<UpdateTaskUseCase.UpdateTaskResult> updateTask(
            @PathVariable Long id,
            @RequestBody @Valid UpdateTaskRequest request) {
        return ApiResponse.success(
                200,
                String.format("Task with id %d updated successfully", id),
                updateTaskUseCase.updateTask(
                        new UpdateTaskUseCase.UpdateTaskCommand(
                                id,
                                request.getTitle(),
                                request.getDescription(),
                                request.getStatus(),
                                request.getPriority(),
                                request.getDueDate()
                        )
                )
        );
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<UpdateTaskUseCase.UpdateTaskStatusResult> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ApiResponse.success(
                200,
                String.format("Status of task with id %d updated successfully", id),
                updateTaskUseCase.updateTaskStatus(
                        new UpdateTaskUseCase.UpdateTaskStatusCommand(
                                id,
                                status
                        )
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<DeleteTaskUseCase.DeleteTaskResult> deleteTask(@PathVariable Long id) {
        return ApiResponse.success(
                200,
                String.format("Task with id %d deleted successfully", id),
                deleteTaskUseCase.deleteTask(
                        new DeleteTaskUseCase.DeleteTaskCommand(id)
                )
        );
    }
}
