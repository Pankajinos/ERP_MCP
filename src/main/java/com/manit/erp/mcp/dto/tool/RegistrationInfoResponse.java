package com.manit.erp.mcp.dto.tool;

import java.util.List;

/**
 * Tool response DTO for getRegistrationInfo(semester).
 */
public record RegistrationInfoResponse(
        Integer semester,
        Double credits,
        List<RegisteredSubjectInfo> subjects
) {
    public record RegisteredSubjectInfo(
            String subjectCode,
            String subjectName,
            String faculty,
            Boolean feedbackPending
    ) {}
}
