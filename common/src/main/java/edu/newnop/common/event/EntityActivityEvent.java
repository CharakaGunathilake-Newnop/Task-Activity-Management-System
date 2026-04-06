package edu.newnop.common.event;

import edu.newnop.common.model.ActionType;

public record EntityActivityEvent(
        String entityName,
        Long entityId,
        ActionType actionType,
        String description,
        Long actorId
) {
}
