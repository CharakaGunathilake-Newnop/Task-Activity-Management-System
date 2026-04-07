package edu.newnop.infrastructure.adapter.in;

import edu.newnop.common.ApiResponse;
import edu.newnop.common.port.UserAdminPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserAdminPort userAdminPort;

    @GetMapping
    public ApiResponse<UserAdminPort.GetAllUsersResult> getAllUsers(
            @PageableDefault(page = 1, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String search
    ) {
        return ApiResponse.success(
                200,
                "Retrieved All Users successfully",
                userAdminPort.getAllUsers(
                        new UserAdminPort.GetTotalUserCountCommand(
                                PageRequest.of(
                                        Math.max(0, pageable.getPageNumber() - 1),
                                        pageable.getPageSize(),
                                        pageable.getSort()
                                ),
                                search
                        )
                )
        );
    }

    @PatchMapping("/{id}")
    public ApiResponse<UserAdminPort.DeactivateUserResult> deactivateUser(@PathVariable Long id) {

        return ApiResponse.success(
                200,
                "User deactivated successfully",
                userAdminPort.deactivateUser(id)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userAdminPort.deleteUser(id);
        return ApiResponse.success(
                200,
                "User deleted successfully",
                null
        );
    }
}
