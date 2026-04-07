package edu.newnop.common.port;

import edu.newnop.common.port.dto.UserResponseDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

public interface UserAdminPort {
    GetAllUsersResult getAllUsers(GetTotalUserCountCommand command);

    record GetTotalUserCountCommand(
            PageRequest pageRequest,
            String searchQuery
    ) {
    }

    record GetAllUsersResult(
            List<UserResponseDto> tasks,
            int page,
            int size,
            long totalItems,
            int totalPages
    ) {
    }

    DeactivateUserResult deactivateUser(Long userId);

    record DeactivateUserResult(
            UserResponseDto userResponseDto
    ) {

    }

    void deleteUser(Long userId);

    Map<String, Long> getUserBreakdown();
}
