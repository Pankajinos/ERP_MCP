package com.manit.erp.mcp.dto.tool;

/**
 * Tool response DTO for getSubjectDetails(subjectCode).
 */
public record SubjectDetailsResponse(
        String subjectCode,
        String subjectName,
        String faculty,
        MarksBreakdown marks,
        String grade,
        Double credits
) {
    public record MarksBreakdown(
            Double midterm,
            Double endterm,
            Double total
    ) {}
}
