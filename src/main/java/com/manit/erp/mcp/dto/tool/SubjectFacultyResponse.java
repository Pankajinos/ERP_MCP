package com.manit.erp.mcp.dto.tool;

public record SubjectFacultyResponse(
        String subjectCode,
        String subjectName,
        String facultyName,
        Integer semester,
        String department
) {}
