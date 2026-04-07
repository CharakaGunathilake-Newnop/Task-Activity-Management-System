package edu.newnop.application.in;

import edu.newnop.domain.model.Activity;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface GetActivityHistoryUseCase {
    GetActivityHistoryResult getActivityHistory(GetActivityHistoryCommand command);

    record GetActivityHistoryCommand(
            PageRequest pageRequest,
            String searchQuery,
            Long selectedUserId
    ) {
    }

    record GetActivityHistoryResult(
            List<Activity> tasks,
            int page,
            int size,
            long totalItems,
            int totalPages
    ) {
    }
}
