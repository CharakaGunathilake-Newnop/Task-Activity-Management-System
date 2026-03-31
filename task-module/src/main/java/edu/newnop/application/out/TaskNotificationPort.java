package edu.newnop.application.out;

import edu.newnop.application.out.dto.NotificationRequest;

public interface TaskNotificationPort {
        void notifyTaskEvent(NotificationRequest<?> request);
}
