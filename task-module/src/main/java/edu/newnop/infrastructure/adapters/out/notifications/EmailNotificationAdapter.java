package edu.newnop.infrastructure.adapters.out.notifications;

import edu.newnop.application.out.TaskNotificationPort;
import edu.newnop.application.out.dto.NotificationRequest;
import edu.newnop.infrastructure.adapters.in.web.dto.TaskResponseDto;
import edu.newnop.infrastructure.adapters.out.notifications.exceptions.EmailServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationAdapter implements TaskNotificationPort {

    private final JavaMailSender mailSender;

    @Value("${application.notification.sender.mail}")
    private String senderMail;

    @Override
    public void notifyTaskEvent(NotificationRequest<?> notificationRequest) {
        final Object data = notificationRequest.getData();
        final String receiver = notificationRequest.getReceiverEmail().trim();
        final String receiverName = notificationRequest.getReceiverName().trim();

        // Use \n for newlines and \t for tabs
        final StringFormattedMessage formattedData = new StringFormattedMessage("Task\n\t ID: %s\n\tTitle: %s\n\tDescription: %s",
                ((TaskResponseDto) data).getId(),
                ((TaskResponseDto) data).getTitle(),
                ((TaskResponseDto) data).getDescription()
        );
        final String body = "Hi " + receiverName + ",\n\n" +
                notificationRequest.getMessage() + "\n\t" +
                formattedData;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(senderMail);
        mailMessage.setTo(receiver);
        mailMessage.setSubject(notificationRequest.getSubject());
        mailMessage.setText(body);

        log.debug("Sending Mail from: {} to: {}", senderMail, receiver);

        try {
            mailSender.send(mailMessage);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", receiver, e.getMessage());
            throw new EmailServiceUnavailableException("Email service unavailable. Please try again later.");
        }
    }
}
