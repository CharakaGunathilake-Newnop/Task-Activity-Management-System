package edu.newnop.application.in;

import edu.newnop.common.model.ActionType;

public interface CreateActivityLogUseCase {
    void createActivityLog(CreateActivityLogCommand command);

    record CreateActivityLogCommand(
            String entityName,
            Long entityId,
            ActionType actionType,
            String description,
            Long actorId
    ) {
    }
}
