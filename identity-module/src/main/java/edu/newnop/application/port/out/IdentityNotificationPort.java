package edu.newnop.application.port.out;

import edu.newnop.application.port.out.dto.NotificationRequest;

public interface IdentityNotificationPort {
    String sendMail(NotificationRequest<?> notificationRequest);
}
