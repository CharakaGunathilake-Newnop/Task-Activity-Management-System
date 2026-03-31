package edu.newnop.application.in;

import edu.newnop.infrastructure.adapters.in.web.dto.TaskResponseDto;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface GetTasksUseCase {
    GetTasksResult getPaginatedTasks(GetTasksCommand command);

    record GetTasksCommand(
            int page,
            int size,
            String sortBy,
            String sortDirection,
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
