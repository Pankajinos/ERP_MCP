package com.manit.erp.mcp.services;

import com.manit.erp.mcp.dto.tool.StudentDashboardResponse;
import reactor.core.publisher.Mono;

/**
 * Service interface for high-level student dashboard summary.
 */
public interface StudentDashboardService {
    Mono<StudentDashboardResponse> getStudentDashboard();
}
