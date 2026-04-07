package edu.newnop.domain.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public record TaskAnalyticsSummary(
        Long totalTasks,
        Long todoTasks,
        Long inProgressTasks,
        Long completeTasks,
        Long lowPriorityTasks,
        Long mediumPriorityTasks,
        Long highPriorityTasks,
        Long upcomingTask,
        Long overdueTasks
) {
    public Map<String, Long> toMap() {
        // Using LinkedHashMap to guarantee the frontend receives the JSON in this exact order
        Map<String, Long> map = new LinkedHashMap<>();

        map.put("totalTasks", totalTasks);

        // Status Breakdown
        map.put("todoTasks", todoTasks);
        map.put("inProgressTasks", inProgressTasks);
        map.put("completeTasks", completeTasks);

        // Priority Breakdown
        map.put("lowPriorityTasks", lowPriorityTasks);
        map.put("mediumPriorityTasks", mediumPriorityTasks);
        map.put("highPriorityTasks", highPriorityTasks);

        // Timeline Breakdown
        map.put("upcomingTasks", upcomingTask);
        map.put("overdueTasks", overdueTasks);

        return map;
    }
}
