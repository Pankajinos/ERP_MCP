package com.manit.erp.mcp.services;

import com.manit.erp.mcp.clients.FeeApiClient;
import com.manit.erp.mcp.clients.RegistrationApiClient;
import com.manit.erp.mcp.clients.ResultApiClient;
import com.manit.erp.mcp.dto.erp.ResultErpResponse;
import com.manit.erp.mcp.dto.tool.AcademicSummaryResponse;
import com.manit.erp.mcp.dto.tool.SemesterDetailsResponse;
import com.manit.erp.mcp.dto.tool.SubjectDetailsResponse;
import com.manit.erp.mcp.exception.ResourceNotFoundException;
import com.manit.erp.mcp.mapper.ErpDataMapper;
import com.manit.erp.mcp.services.impl.AcademicServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcademicServiceTest {

    @Mock
    private ResultApiClient resultApiClient;

    @Mock
    private RegistrationApiClient registrationApiClient;

    @Mock
    private FeeApiClient feeApiClient;

    private AcademicService academicService;

    @BeforeEach
    void setUp() {
        ErpDataMapper mapper = new ErpDataMapper();
        academicService = new AcademicServiceImpl(resultApiClient, registrationApiClient, feeApiClient, mapper);
    }

    @Test
    void testGetAcademicSummary_Success() {
        ResultErpResponse.BasicDetail basic = new ResultErpResponse.BasicDetail(
                "Pankaj Soni", "CSE", "Computer Science", "CSE", "123", "REG123", "M", "email@manit.ac.in", 5, "Sem 5", 4705, "B.Tech"
        );
        ResultErpResponse.GrandTotal gt = new ResultErpResponse.GrandTotal(
                500.0, 420.0, 84.0, "8.4", 20.0, 168.0, 200.0, "FIRST", "REGULAR", "PASS", "2024-2025"
        );
        ResultErpResponse.SemesterDataItem semItem = new ResultErpResponse.SemesterDataItem("SUCCESS", "OK", new ResultErpResponse.SemesterInnerData(List.of(), gt));
        ResultErpResponse mockResponse = new ResultErpResponse("SUCCESS", new ResultErpResponse.ResultData(List.of(basic), List.of(semItem)));

        when(resultApiClient.fetchStudentResult(any(), any())).thenReturn(Mono.just(mockResponse));

        Mono<AcademicSummaryResponse> resultMono = academicService.getAcademicSummary();

        StepVerifier.create(resultMono)
                .assertNext(summary -> {
                    assertEquals("Pankaj Soni", summary.studentName());
                    assertEquals("Computer Science", summary.program());
                    assertEquals(8.4, summary.cgpa());
                    assertEquals(1, summary.semesterBreakdown().size());
                    assertEquals(8.4, summary.semesterBreakdown().get(0).sgpa());
                })
                .verifyComplete();
    }

    @Test
    void testGetSubjectDetails_NotFound() {
        when(resultApiClient.fetchStudentResult(any(), any())).thenReturn(Mono.just(new ResultErpResponse("SUCCESS", new ResultErpResponse.ResultData(List.of(), List.of()))));
        when(registrationApiClient.fetchRegistrationInfo(any(), any())).thenReturn(Mono.just(List.of()));

        Mono<SubjectDetailsResponse> mono = academicService.getSubjectDetails("INVALID999");

        StepVerifier.create(mono)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void testGetSemesterDetails_InvalidSemester() {
        Mono<SemesterDetailsResponse> mono = academicService.getSemesterDetails(0);

        StepVerifier.create(mono)
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
