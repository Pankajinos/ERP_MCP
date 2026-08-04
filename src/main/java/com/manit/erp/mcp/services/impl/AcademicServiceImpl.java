package com.manit.erp.mcp.services.impl;

import com.manit.erp.mcp.clients.FeeApiClient;
import com.manit.erp.mcp.clients.RegistrationApiClient;
import com.manit.erp.mcp.clients.ResultApiClient;
import com.manit.erp.mcp.dto.erp.FeeErpResponse;
import com.manit.erp.mcp.dto.erp.RegistrationErpResponse;
import com.manit.erp.mcp.dto.erp.ResultErpResponse;
import com.manit.erp.mcp.dto.tool.AcademicSearchResultResponse;
import com.manit.erp.mcp.dto.tool.AcademicSummaryResponse;
import com.manit.erp.mcp.dto.tool.SemesterDetailsResponse;
import com.manit.erp.mcp.dto.tool.SubjectDetailsResponse;
import com.manit.erp.mcp.exception.ResourceNotFoundException;
import com.manit.erp.mcp.mapper.ErpDataMapper;
import com.manit.erp.mcp.services.AcademicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Production implementation of AcademicService using non-blocking WebClient calls and reactive composition.
 */
@Service
public class AcademicServiceImpl implements AcademicService {

    private static final Logger log = LoggerFactory.getLogger(AcademicServiceImpl.class);

    private final ResultApiClient resultApiClient;
    private final RegistrationApiClient registrationApiClient;
    private final FeeApiClient feeApiClient;
    private final ErpDataMapper erpDataMapper;

    public AcademicServiceImpl(
            ResultApiClient resultApiClient,
            RegistrationApiClient registrationApiClient,
            FeeApiClient feeApiClient,
            ErpDataMapper erpDataMapper
    ) {
        this.resultApiClient = resultApiClient;
        this.registrationApiClient = registrationApiClient;
        this.feeApiClient = feeApiClient;
        this.erpDataMapper = erpDataMapper;
    }

    @Override
    public Mono<AcademicSummaryResponse> getAcademicSummary() {
        log.info("Processing getAcademicSummary service request");
        return resultApiClient.fetchStudentResult(null, null)
                .map(erpDataMapper::toAcademicSummary);
    }

    @Override
    public Mono<SemesterDetailsResponse> getSemesterDetails(int semester) {
        log.info("Processing getSemesterDetails for semester: {}", semester);
        if (semester <= 0) {
            return Mono.error(new IllegalArgumentException("Semester number must be positive (>= 1)."));
        }

        Mono<ResultErpResponse> resultMono = resultApiClient.fetchStudentResult(null, null)
                .onErrorReturn(new ResultErpResponse("ERROR", null));
        Mono<List<RegistrationErpResponse>> regMono = registrationApiClient.fetchRegistrationInfo(null, null)
                .onErrorReturn(List.of());
        Mono<FeeErpResponse> feeMono = feeApiClient.fetchStudentFees(null, null)
                .onErrorReturn(new FeeErpResponse(List.of()));

        return Mono.zip(resultMono, regMono, feeMono)
                .map(tuple -> erpDataMapper.toSemesterDetails(semester, tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    @Override
    public Mono<SubjectDetailsResponse> getSubjectDetails(String subjectCode) {
        log.info("Processing getSubjectDetails for subject code: {}", subjectCode);
        if (subjectCode == null || subjectCode.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Subject code must not be null or empty."));
        }

        Mono<ResultErpResponse> resultMono = resultApiClient.fetchStudentResult(null, null)
                .onErrorReturn(new ResultErpResponse("ERROR", null));
        Mono<List<RegistrationErpResponse>> regMono = registrationApiClient.fetchRegistrationInfo(null, null)
                .onErrorReturn(List.of());

        return Mono.zip(resultMono, regMono)
                .flatMap(tuple -> {
                    SubjectDetailsResponse response = erpDataMapper.toSubjectDetails(subjectCode, tuple.getT1(), tuple.getT2());
                    if (response == null) {
                        return Mono.error(new ResourceNotFoundException("Subject with code '" + subjectCode + "' was not found."));
                    }
                    return Mono.just(response);
                });
    }

    @Override
    public Mono<AcademicSearchResultResponse> searchAcademicRecords(String query) {
        log.info("Processing searchAcademicRecords for query: {}", query);
        Mono<ResultErpResponse> resultMono = resultApiClient.fetchStudentResult(null, null)
                .onErrorReturn(new ResultErpResponse("ERROR", null));
        Mono<List<RegistrationErpResponse>> regMono = registrationApiClient.fetchRegistrationInfo(null, null)
                .onErrorReturn(List.of());

        return Mono.zip(resultMono, regMono)
                .map(tuple -> erpDataMapper.searchAcademicRecords(query, tuple.getT1(), tuple.getT2()));
    }
}
