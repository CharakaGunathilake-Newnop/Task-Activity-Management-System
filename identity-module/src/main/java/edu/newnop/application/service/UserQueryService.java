package edu.newnop.application.service;

import edu.newnop.application.port.out.UserQueryPort;
import edu.newnop.application.port.out.UserRepositoryPort;
import edu.newnop.common.model.UserNotificationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserQueryService implements UserQueryPort {
    private final UserRepositoryPort userRepository;

    @Override
    public UserNotificationInfo findUserNotificationInfoById(Long userId) {
        return userRepository.findById(userId)
                .map(u -> new UserNotificationInfo(u.getId(), u.getEmail(), u.getName()))
                .orElse(null);
    }

    @Override
    public List<UserNotificationInfo> findAllUsersNotificationInfoByIds(Long[] userIds) {
        return userRepository.findAllByIdIn(userIds).stream()
                .filter(Objects::nonNull)
                .map(u -> new UserNotificationInfo(u.getId(), u.getEmail(), u.getName()))
                .toList();
    }
}
