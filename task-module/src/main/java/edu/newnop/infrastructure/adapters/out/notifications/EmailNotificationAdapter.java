package edu.newnop.infrastructure.adapters.out.notifications;

import edu.newnop.application.in.CreateTaskUseCase;
import edu.newnop.application.in.DeleteTaskUseCase;
import edu.newnop.application.in.UpdateTaskUseCase;
import edu.newnop.application.out.TaskNotificationPort;
import edu.newnop.application.out.dto.NotificationRequest;
import edu.newnop.infrastructure.adapters.in.web.dto.TaskResponseDto;
import edu.newnop.infrastructure.adapters.in.web.dto.UpdateTaskRequest;
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
        final StringFormattedMessage formattedData = formatData(data);

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

    private StringFormattedMessage formatData(Object data) {
        final String message = "\nTask\n\t ID: %s\n\tTitle: %s\n\tDescription: %s \n\tStatus: %s\n\tPriority: %s\n\tDue Date: %s";
        return switch (data) {
            case TaskResponseDto task -> new StringFormattedMessage(message,
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getStatus(),
                    task.getPriority(),
                    task.getDueDate()
            );

            case CreateTaskUseCase.CreateTaskResult task -> new StringFormattedMessage(message,
                    task.id(),
                    task.title(),
                    task.description(),
                    task.status(),
                    task.priority(),
                    task.dueDate()
            );

            case UpdateTaskUseCase.UpdateTaskResult task -> new StringFormattedMessage(message,
                    task.taskId(),
                    task.title(),
                    task.description(),
                    task.status(),
                    task.priority(),
                    task.dueDate()
            );

            case UpdateTaskUseCase.UpdateTaskStatusResult task -> new StringFormattedMessage(
                    "\nTask\n\t ID: %s\n\tStatus: %s",
                    task.taskId(),
                    task.status()
            );

            case DeleteTaskUseCase.DeleteTaskResult task -> new StringFormattedMessage(
                    "\n\tMessage: %s",
                    task.message()
            );

            // Fallback for types you haven't handled yet
            default -> new StringFormattedMessage("Update received for: %s", data.getClass().getSimpleName());
        };
    }

}
