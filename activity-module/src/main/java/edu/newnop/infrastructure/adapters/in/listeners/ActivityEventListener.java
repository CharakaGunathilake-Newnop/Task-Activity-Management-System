package edu.newnop.infrastructure.adapters.in.listeners;

import edu.newnop.application.in.CreateActivityLogUseCase;
import edu.newnop.common.event.EntityActivityEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ActivityEventListener {
    private final CreateActivityLogUseCase createActivityLogUseCase;

    /**
     * Handle an entity activity event and create an activity log entry based on it.
     * This method is called after the transaction is committed.
     * @param event the entity activity event to be handled
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleActivityEvent(EntityActivityEvent event) {

        createActivityLogUseCase.createActivityLog(new CreateActivityLogUseCase.CreateActivityLogCommand(
                event.entityName(),
                event.entityId(),
                event.actionType(),
                event.description(),
                event.actorId()
        ));
    }
}
