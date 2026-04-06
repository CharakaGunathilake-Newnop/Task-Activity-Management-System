package edu.newnop.application.service;

import edu.newnop.application.in.CreateActivityLogUseCase;
import edu.newnop.application.out.ActivityLogRepositoryPort;
import edu.newnop.domain.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogService implements CreateActivityLogUseCase {

    private final ActivityLogRepositoryPort activityLogRepositoryPort;

    @Override
    public void createActivityLog(CreateActivityLogCommand command) {
        log.info(" Actor ID: {}, Action Type: {}, Entity Name: {}, Entity ID: {}, Description: {}",
                command.actorId(), command.actionType(), command.entityName(), command.entityId(), command.description());

        activityLogRepositoryPort.save(new Activity(
                command.entityName(),
                command.entityId(),
                command.actionType(),
                command.description(),
                command.actorId()
        ));
    }
}
