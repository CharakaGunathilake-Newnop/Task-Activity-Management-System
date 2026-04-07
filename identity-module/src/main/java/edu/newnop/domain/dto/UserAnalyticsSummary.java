package edu.newnop.domain.dto;

public record UserAnalyticsSummary(
        Long totalUsers,
        Long enabledUsers,
        Long verifiedUsers,
        Long activeUsers,
        Long inactiveUsers,
        Long adminUsers,
        Long generalUsers
) {
}
