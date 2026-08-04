package com.manit.erp.mcp.dto.tool;

import java.util.List;

/**
 * Tool response DTO for getAcademicSummary().
 */
public record AcademicSummaryResponse(
        String studentName,
        String program,
        Double cgpa,
        List<SemesterBreakdown> semesterBreakdown
) {
    public record SemesterBreakdown(
            Integer semester,
            Double sgpa,
            Double credits
    ) {}
}
