package com.manit.erp.mcp.dto.tool;

import java.util.List;

/**
 * Tool response DTO for getSemesterDetails(semester).
 */
public record SemesterDetailsResponse(
        Integer semester,
        Double sgpa,
        Double credits,
        List<SubjectDetail> subjects,
        FeeSummary fees
) {
    public record SubjectDetail(
            String subjectCode,
            String subjectName,
            String faculty
    ) {}

    public record FeeSummary(
            Double academic,
            Double hostel,
            Double other
    ) {}
}
