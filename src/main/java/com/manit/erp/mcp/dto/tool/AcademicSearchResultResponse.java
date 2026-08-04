package com.manit.erp.mcp.dto.tool;

import java.util.List;

/**
 * Tool response DTO for searchAcademicRecords(query).
 */
public record AcademicSearchResultResponse(
        List<SearchMatch> matches
) {
    public record SearchMatch(
            String subject,
            String subjectCode,
            String grade,
            Double marks,
            Double credits,
            Integer semester,
            String detail
    ) {}
}
