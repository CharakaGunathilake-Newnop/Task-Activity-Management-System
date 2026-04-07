package edu.newnop.application.service;

import edu.newnop.application.in.CreateActivityLogUseCase;
import edu.newnop.application.in.GetActivityHistoryUseCase;
import edu.newnop.application.out.ActivityLogRepositoryPort;
import edu.newnop.common.security.AuthenticatedUser;
import edu.newnop.domain.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogService implements CreateActivityLogUseCase, GetActivityHistoryUseCase {

    private final ActivityLogRepositoryPort activityLogRepositoryPort;

    @Override
    public void createActivityLog(CreateActivityLogCommand command) {
        log.info(" Actor ID: {}, Action Type: {}, Entity Name: {}, Entity ID: {}, Description: {}",
                command.actorId(), command.actionType(), command.entityName(), command.entityId(), command.description());

        activityLogRepositoryPort.save(new Activity(
                command.entityName(),
                command.entityId(),
                command.actionType(),
                command.description(),
                command.actorId()
        ));
    }

    @Override
    public GetActivityHistoryResult getActivityHistory(GetActivityHistoryCommand command) {
        // Validate and sanitize pagination parameters
        Pageable pageable = command.pageRequest();
        PageRequest pageRequest = PageRequest.of(
                Math.max(0, pageable.getPageNumber()), // Ensure page number is non-negative
                pageable.getPageSize() < 0 ? 10 : Math.min(pageable.getPageSize(), 100), // Default page size to 10 and cap at 100
                pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(Sort.Direction.DESC, "createdAt") // Default sorting by createdAt desc
        );

        final String searchQuery = command.searchQuery() != null ? command.searchQuery().trim() : "";

        final Long selectedUserId = command.selectedUserId();

        final AuthenticatedUser user = getUser();

        final List<String> userRoles = user.authorities().stream().map(GrantedAuthority::getAuthority).toList();

        final boolean isAdmin = userRoles.contains("ROLE_ADMIN");

        final Long actorId = isAdmin ? selectedUserId : user.userId(); // Admin can see all activities, regular users only their own

        Page<Activity> activities;

        // If user is admin and no specific actorId is provided, return all tasks with search query
        if (isAdmin && null == selectedUserId) {
            activities = searchQuery.isEmpty() ? activityLogRepositoryPort.findAll(pageRequest) : activityLogRepositoryPort.findAllWithSearchQuery(searchQuery, pageRequest);
        } else if (searchQuery.isEmpty()) {
            activities = activityLogRepositoryPort.findAllByUserId(actorId, pageRequest);
        } else {
            // Implement a custom search query with filtering by entityName and actionType
            activities = activityLogRepositoryPort.findByUserIdAndSearchQuery(actorId, searchQuery, pageRequest);
        }

        log.info("{} ID [{}] with email: {} requested activity history page: [{}] {} {}",
                isAdmin ? "ADMIN" : "USER",
                user.userId(),
                user.email(),
                pageable.getPageNumber(),
                searchQuery.isEmpty() ? "" : "search by [" + searchQuery + "]",
                actorId != null ? "of user with id: [" + actorId + "]" : "of all users"
        );

        return new GetActivityHistoryResult(
                activities.getContent(),
                activities.getNumber() == 0 ? 1 : activities.getNumber() + 1, // Convert to 1-based page number for client
                activities.getSize(),
                activities.getTotalElements(),
                activities.getTotalPages()
        );
    }

    private AuthenticatedUser getUser() {
        try {
            return (AuthenticatedUser) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        } catch (NullPointerException e) {
            throw new UsernameNotFoundException("No authenticated user found in security context");
        }
    }
}
