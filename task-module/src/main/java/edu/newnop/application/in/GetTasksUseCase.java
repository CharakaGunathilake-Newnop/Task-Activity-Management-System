package edu.newnop.application.in;

import edu.newnop.infrastructure.adapters.in.web.dto.TaskResponseDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface GetTasksUseCase {
    GetTasksResult getPaginatedTasks(GetTasksCommand command);

    record GetTasksCommand(
            PageRequest pageRequest,
            String searchQuery,
            Long selectedUserId
    ) {
    }

    record GetTasksResult(
            List<TaskResponseDto> tasks,
            int page,
            int size,
            long totalItems,
            int totalPages
    ) {
    }
}
