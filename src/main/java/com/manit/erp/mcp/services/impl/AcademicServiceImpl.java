package com.manit.erp.mcp.services.impl;

import com.manit.erp.mcp.clients.RegistrationApiClient;
import com.manit.erp.mcp.clients.ResultApiClient;
import com.manit.erp.mcp.dto.tool.SubjectFacultyResponse;
import com.manit.erp.mcp.dto.tool.SubjectMarksResponse;
import com.manit.erp.mcp.mapper.ErpDataMapper;
import com.manit.erp.mcp.services.AcademicService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of AcademicService handling granular subject marks and subject faculty lookups.
 */
@Service
@RequiredArgsConstructor
public class AcademicServiceImpl implements AcademicService {

    private static final Logger log = LoggerFactory.getLogger(AcademicServiceImpl.class);

    private final ResultApiClient resultApiClient;
    private final RegistrationApiClient registrationApiClient;
    private final ErpDataMapper erpDataMapper;


    @Override
    public Mono<SubjectMarksResponse> getSubjectMarks(String subject, String authorizationHeader) {
        log.info("Processing getSubjectMarks for subject: {}", subject);
        if (subject == null || subject.isBlank()) {
            return Mono.error(new IllegalArgumentException("Subject code or name must not be empty."));
        }

        return resultApiClient.fetchStudentResult(null, null, authorizationHeader)
                .map(resultErp -> erpDataMapper.toSubjectMarks(subject, resultErp));
    }

    @Override
    public Mono<SubjectFacultyResponse> getSubjectFaculty(String subject, String authorizationHeader) {
        log.info("Processing getSubjectFaculty for subject: {}", subject);
        if (subject == null || subject.isBlank()) {
            return Mono.error(new IllegalArgumentException("Subject code or name must not be empty."));
        }

        return registrationApiClient.fetchRegistrationInfo(null, null, authorizationHeader)
                .map(regList -> erpDataMapper.toSubjectFaculty(subject, regList));
    }
}
