package edu.newnop.infrastructure.adapters.in.web;


import edu.newnop.application.in.CreateTaskUseCase;
import edu.newnop.common.ApiResponse;
import edu.newnop.infrastructure.adapters.in.web.dto.CreateTaskRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {
    private final CreateTaskUseCase createTaskUseCase;

    @PostMapping
    public ApiResponse<CreateTaskUseCase.CreateTaskResult> createTask(Principal userId, @RequestBody @Valid CreateTaskRequest request) {
        return ApiResponse.success(
                201,
                String.format("Task '%s' created successfully", request.getTitle()),
                createTaskUseCase.createTask(
                        new CreateTaskUseCase.CreateTaskCommand(
                                request.getTitle(),
                                request.getDescription(),
                                request.getStatus(),
                                request.getPriority(),
                                request.getDueDate(),
                                userId.getName()
                        )
                )
        );
    }
}
