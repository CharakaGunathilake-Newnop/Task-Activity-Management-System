package edu.newnop.infrastructure.adapters.out.notification;

import edu.newnop.application.port.out.IdentityNotificationPort;
import edu.newnop.application.port.out.dto.NotificationRequest;
import edu.newnop.infrastructure.adapters.in.web.exceptions.EmailServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmtpEmailAdapter implements IdentityNotificationPort {

    private final JavaMailSender mailSender;

    @Value("${application.notification.sender.mail}")
    private String senderMail;

    @Override
    public String sendMail(NotificationRequest<?> notificationRequest) {
        final Object data = notificationRequest.getData();
        final String receiver = notificationRequest.getReceiverEmail().trim();
        final String receiverName = notificationRequest.getReceiverName().trim();

        // Fix: Use \n for newlines and \t for tabs
        final String body = "Hi " + receiverName + ",\n\n" +
                notificationRequest.getMessage() + "\n\t" +
                (null != data ? data : "");

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(senderMail);
        mailMessage.setTo(receiver);
        mailMessage.setSubject(notificationRequest.getSubject());
        mailMessage.setText(body);

        log.debug("Sending Mail from: {} to: {}", senderMail, receiver);

        try {
            mailSender.send(mailMessage);
            return "Mail sent successfully to: " + receiver;
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", receiver, e.getMessage());
            throw new EmailServiceUnavailableException("Email service unavailable. Please try again later.");
        }
    }
}
