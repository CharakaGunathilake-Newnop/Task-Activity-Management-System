package edu.newnop.common.port;

import java.util.Map;

public interface TaskAdminPort {
    Map<String, Long> getTaskStatusBreakdown();
}