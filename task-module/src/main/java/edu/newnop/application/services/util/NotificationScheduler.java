package edu.newnop.application.services.util;


import edu.newnop.application.out.TaskNotificationPort;
import edu.newnop.application.out.TaskRepositoryPort;
import edu.newnop.application.out.dto.NotificationRequest;
import edu.newnop.common.model.UserNotificationInfo;
import edu.newnop.common.model.UserNotificationInfoQueryPort;
import edu.newnop.domain.model.Task;
import edu.newnop.infrastructure.adapters.in.web.dto.TaskResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final TaskNotificationPort notificationService;
    private final TaskRepositoryPort taskRepository;
    private final UserNotificationInfoQueryPort userQueryPort;

    @Transactional
    @Scheduled(fixedDelay = 3600000 * 24) // Every 24 hours
    public void processTaskNotifications() {
        log.info("Starting scheduled notification check at {}", LocalDateTime.now());

        // Fetch tasks due within the next 24 hours (includes overdue) that haven't been notified
        Date threshold = Date.from(LocalDateTime.now().plusHours(24).atZone(ZoneId.systemDefault()).toInstant());
        List<Task> tasks = taskRepository.findAllByDueDateIsBeforeAndNotificationSentFalse(threshold);

        if (tasks.isEmpty()) {
            log.info("No pending notifications found.");
            return;
        }

        Long[] userIds = tasks.stream().map(Task::getAssignedUserId).distinct().toArray(Long[]::new);

        List<UserNotificationInfo> userInfos = userQueryPort.findAllUsersNotificationInfoByIds(userIds);

        for (Task task : tasks) {
            UserNotificationInfo userInfo = userInfos.stream()
                    .filter(u -> u.id().equals(task.getAssignedUserId()))
                    .findFirst()
                    .orElse(null);

            if (userInfo == null) {
                log.warn("No user info found for Task ID: {}. Skipping notification.", task.getId());
                continue;
            }

            try {
                // Distinguish between Overdue and Upcoming for the message content
                if (task.getDueDate().before(new Date())) {
                    notificationService.notifyTaskEvent(
                            new NotificationRequest<>(
                                    userInfo.email(),
                                    userInfo.name().split(" ")[0], // First name for personalization
                                    "Your task is overdue! Please take immediate action.",
                                    "Task Overdue: " + task.getTitle(),
                                    new TaskResponseDto().fromDomain(task)
                            )
                    );
                } else {
                    notificationService.notifyTaskEvent(
                            new NotificationRequest<>(
                                    userInfo.email(),
                                    userInfo.name().split(" ")[0], // First name for personalization
                                    "You have a task due at " + task.getDueDate() + ". Please make sure to complete it on time.",
                                    "Upcoming Task: " + task.getTitle(),
                                    new TaskResponseDto().fromDomain(task)
                            )
                    );
                }

                // Mark as notified and persist to the DB
                task.setNotificationSent(true);
                taskRepository.save(task);

                log.info("Notification sent successfully for Task ID: {}", task.getId());
            } catch (Exception e) {
                // Don't let one failed email stop the entire loop
                log.error("Failed to send notification for Task ID: {}. Error: {}", task.getId(), e.getMessage());
            }
        }
    }
}
