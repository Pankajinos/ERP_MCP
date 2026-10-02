package com.manit.erp.mcp.dto.tool;

public record SubjectMarksResponse(
        String subjectCode,
        String subjectName,
        Integer semester,
        Double midTermMarks,
        Double endTermMarks,
        Double marksObtained,
        Double totalMarks,
        String grade,
        String gradePoint,
        Double credit
) {}
