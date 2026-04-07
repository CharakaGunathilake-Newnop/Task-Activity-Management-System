package edu.newnop.infrastructure.adapter.in;

import edu.newnop.common.ApiResponse;
import edu.newnop.common.port.TaskAdminPort;
import edu.newnop.common.port.UserAdminPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final TaskAdminPort taskAdminPort;
    private final UserAdminPort userAdminPort;

    @GetMapping
    public ApiResponse<Map<String, Object>> getAnalytics() {

        Map<String, Object> users = Map.of(
                "usersBreakdown", userAdminPort.getUserBreakdown()
        );

        Map<String, Object> tasks = Map.of(
                "taskBreakDown", taskAdminPort.getTaskStatusBreakdown()
        );

        return ApiResponse.success(
                200,
                "Analytics",
                Map.of(
                        "totalUsers", users,
                        "tasks", tasks
                )
        );
    }

}
