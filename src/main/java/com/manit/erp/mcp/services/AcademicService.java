package com.manit.erp.mcp.services;

import com.manit.erp.mcp.dto.tool.SubjectFacultyResponse;
import com.manit.erp.mcp.dto.tool.SubjectMarksResponse;
import reactor.core.publisher.Mono;

public interface AcademicService {

    Mono<SubjectMarksResponse> getSubjectMarks(String subject, String authorizationHeader);

    Mono<SubjectFacultyResponse> getSubjectFaculty(String subject, String authorizationHeader);
}
