package com.manit.erp.mcp.services.impl;

import com.manit.erp.mcp.clients.FeeApiClient;
import com.manit.erp.mcp.clients.RegistrationApiClient;
import com.manit.erp.mcp.clients.ResultApiClient;
import com.manit.erp.mcp.dto.erp.FeeErpResponse;
import com.manit.erp.mcp.dto.erp.RegistrationErpResponse;
import com.manit.erp.mcp.dto.erp.ResultErpResponse;
import com.manit.erp.mcp.dto.tool.StudentDashboardResponse;
import com.manit.erp.mcp.mapper.ErpDataMapper;
import com.manit.erp.mcp.services.StudentDashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Implementation of StudentDashboardService combining Result, Registration, and Fee APIs.
 */
@Service
public class StudentDashboardServiceImpl implements StudentDashboardService {

    private static final Logger log = LoggerFactory.getLogger(StudentDashboardServiceImpl.class);

    private final ResultApiClient resultApiClient;
    private final RegistrationApiClient registrationApiClient;
    private final FeeApiClient feeApiClient;
    private final ErpDataMapper erpDataMapper;

    public StudentDashboardServiceImpl(
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
    public Mono<StudentDashboardResponse> getStudentDashboard() {
        log.info("Processing getStudentDashboard service request");

        Mono<ResultErpResponse> resultMono = resultApiClient.fetchStudentResult(null, null)
                .onErrorReturn(new ResultErpResponse("ERROR", null));
        Mono<List<RegistrationErpResponse>> regMono = registrationApiClient.fetchRegistrationInfo(null, null)
                .onErrorReturn(List.of());
        Mono<FeeErpResponse> feeMono = feeApiClient.fetchStudentFees(null, null)
                .onErrorReturn(new FeeErpResponse(List.of()));

        return Mono.zip(resultMono, regMono, feeMono)
                .map(tuple -> erpDataMapper.toStudentDashboard(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }
}
