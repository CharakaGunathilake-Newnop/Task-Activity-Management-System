package edu.newnop.infrastructure.adapters.out.notifications;

import edu.newnop.application.out.TaskNotificationPort;
import edu.newnop.application.out.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationAdapter implements TaskNotificationPort {

    @Override
    public void notifyTaskCreated(NotificationRequest<?> request) {

    }
}
