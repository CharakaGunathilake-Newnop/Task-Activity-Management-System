package edu.newnop.infrastructure.adapters.in.web;

import edu.newnop.application.in.GetActivityHistoryUseCase;
import edu.newnop.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activity")
public class ActivityController {
    private final GetActivityHistoryUseCase getActivityHistoryUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<GetActivityHistoryUseCase.GetActivityHistoryResult> getActivityHistory(
            @PageableDefault(page = 1, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long selectedUserId) {
        return ApiResponse.success(
                200,
                "Tasks retrieved successfully",
                getActivityHistoryUseCase.getActivityHistory(
                        new GetActivityHistoryUseCase.GetActivityHistoryCommand(
                                PageRequest.of(
                                        Math.max(0, pageable.getPageNumber() - 1),
                                        pageable.getPageSize(),
                                        pageable.getSort()
                                ),
                                search,
                                selectedUserId
                        )
                )
        );
    }
}
