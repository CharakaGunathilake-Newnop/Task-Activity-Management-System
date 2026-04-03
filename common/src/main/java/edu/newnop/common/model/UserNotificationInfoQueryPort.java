package edu.newnop.common.model;

import java.util.List;

public interface UserNotificationInfoQueryPort {
    UserNotificationInfo findUserNotificationInfoById(Long userId);
    List<UserNotificationInfo> findAllUsersNotificationInfoByIds(Long[] userIds);
}
