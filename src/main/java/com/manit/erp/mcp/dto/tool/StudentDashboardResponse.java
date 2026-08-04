package com.manit.erp.mcp.dto.tool;

/**
 * Tool response DTO for getStudentDashboard().
 */
public record StudentDashboardResponse(
        String student,
        Integer currentSemester,
        Double cgpa,
        Double credits,
        Integer backlogs,
        Double pendingFees
) {}
