package com.manit.erp.mcp.services;

import com.manit.erp.mcp.dto.tool.AcademicSearchResultResponse;
import com.manit.erp.mcp.dto.tool.AcademicSummaryResponse;
import com.manit.erp.mcp.dto.tool.SemesterDetailsResponse;
import com.manit.erp.mcp.dto.tool.SubjectDetailsResponse;
import reactor.core.publisher.Mono;

/**
 * Service interface encapsulating academic metrics and search capabilities.
 */
public interface AcademicService {
    Mono<AcademicSummaryResponse> getAcademicSummary();
    Mono<SemesterDetailsResponse> getSemesterDetails(int semester);
    Mono<SubjectDetailsResponse> getSubjectDetails(String subjectCode);
    Mono<AcademicSearchResultResponse> searchAcademicRecords(String query);
}
