package com.manit.erp.mcp.services;

import com.manit.erp.mcp.clients.RegistrationApiClient;
import com.manit.erp.mcp.clients.ResultApiClient;
import com.manit.erp.mcp.dto.erp.RegistrationErpResponse;
import com.manit.erp.mcp.dto.erp.ResultErpResponse;
import com.manit.erp.mcp.dto.tool.SubjectFacultyResponse;
import com.manit.erp.mcp.dto.tool.SubjectMarksResponse;
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

    private AcademicService academicService;

    @BeforeEach
    void setUp() {
        ErpDataMapper mapper = new ErpDataMapper();
        academicService = new AcademicServiceImpl(resultApiClient, registrationApiClient, mapper);
    }

    private ResultErpResponse createSampleResultResponse() {
        ResultErpResponse.SubjectItem s1 = new ResultErpResponse.SubjectItem(
                "Data Mining", "MDS323", 100.0, 78.0, 39.0, 39.0, 40.0, 40.0,
                "A", "8.0", "3.0", "2024-2025", 5, 101, "Dec", "2024"
        );
        ResultErpResponse.SemesterInnerData inner = new ResultErpResponse.SemesterInnerData(List.of(s1), null);
        ResultErpResponse.SemesterDataItem semItem = new ResultErpResponse.SemesterDataItem("SUCCESS", "OK", inner);
        ResultErpResponse.SemesterDataItem emptySem = new ResultErpResponse.SemesterDataItem("SUCCESS", "OK", new ResultErpResponse.SemesterInnerData(List.of(), null));
        return new ResultErpResponse("SUCCESS", new ResultErpResponse.ResultData(List.of(), List.of(emptySem, emptySem, emptySem, emptySem, semItem)));
    }

    private List<RegistrationErpResponse> createSampleRegistrationResponse() {
        RegistrationErpResponse.RegisteredSubject rs1 = new RegistrationErpResponse.RegisteredSubject(
                101, 1, "MDS323", "Data Mining", "MDS323", "DESC", "Dr. Ali Ahmed", 55, 1, true
        );
        RegistrationErpResponse reg = new RegistrationErpResponse(
                "2024-2025", 5, "ACTIVE", "2024-08-01", 5, "PAID", "50000", "21.0",
                "Pankaj Soni", "M", "12345", "Computer Science", List.of(rs1)
        );
        return List.of(reg);
    }

    @Test
    void testGetSubjectMarks_SuccessByCode() {
        when(resultApiClient.fetchStudentResult(any(), any())).thenReturn(Mono.just(createSampleResultResponse()));

        Mono<SubjectMarksResponse> mono = academicService.getSubjectMarks("MDS323");

        StepVerifier.create(mono)
                .assertNext(res -> {
                    assertEquals("MDS323", res.subjectCode());
                    assertEquals("Data Mining", res.subjectName());
                    assertEquals(78.0, res.marksObtained());
                    assertEquals(39.0, res.midTermMarks());
                    assertEquals(39.0, res.endTermMarks());
                    assertEquals("A", res.grade());
                    assertEquals(5, res.semester());
                })
                .verifyComplete();
    }

    @Test
    void testGetSubjectMarks_SuccessByName() {
        when(resultApiClient.fetchStudentResult(any(), any())).thenReturn(Mono.just(createSampleResultResponse()));

        Mono<SubjectMarksResponse> mono = academicService.getSubjectMarks("data mining");

        StepVerifier.create(mono)
                .assertNext(res -> {
                    assertEquals("MDS323", res.subjectCode());
                })
                .verifyComplete();
    }

    @Test
    void testGetSubjectMarks_NotFound() {
        when(resultApiClient.fetchStudentResult(any(), any())).thenReturn(Mono.just(createSampleResultResponse()));

        Mono<SubjectMarksResponse> mono = academicService.getSubjectMarks("NON_EXISTENT");

        StepVerifier.create(mono)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void testGetSubjectMarks_EmptyInput() {
        Mono<SubjectMarksResponse> mono = academicService.getSubjectMarks("");

        StepVerifier.create(mono)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void testGetSubjectFaculty_SuccessByCode() {
        when(registrationApiClient.fetchRegistrationInfo(any(), any())).thenReturn(Mono.just(createSampleRegistrationResponse()));

        Mono<SubjectFacultyResponse> mono = academicService.getSubjectFaculty("MDS323");

        StepVerifier.create(mono)
                .assertNext(res -> {
                    assertEquals("MDS323", res.subjectCode());
                    assertEquals("Data Mining", res.subjectName());
                    assertEquals("Dr. Ali Ahmed", res.facultyName());
                    assertEquals(5, res.semester());
                    assertEquals("Computer Science", res.department());
                })
                .verifyComplete();
    }

            @Test
            void testGetSubjectFaculty_UsesRegistrationSemester() {
            RegistrationErpResponse.RegisteredSubject subject = new RegistrationErpResponse.RegisteredSubject(
                321, 1, "MDS321", "Optimization Technique", "MDS321", "DESC",
                "Dr. Madhvi Shakya", 55, 1, true
            );
            RegistrationErpResponse registration = new RegistrationErpResponse(
                "2024-2025", 9, "ACTIVE", "2024-08-01", 1, "PAID", "50000", "21.0",
                "Student", "M", "12345", "Department of Mathematics, Bioinformatics and Computer Applications",
                List.of(subject)
            );
            when(registrationApiClient.fetchRegistrationInfo(any(), any())).thenReturn(Mono.just(List.of(registration)));

            StepVerifier.create(academicService.getSubjectFaculty("MDS321"))
                .assertNext(res -> {
                    assertEquals("Optimization Technique", res.subjectName());
                    assertEquals("Dr. Madhvi Shakya", res.facultyName());
                    assertEquals(9, res.semester());
                    assertEquals("Department of Mathematics, Bioinformatics and Computer Applications", res.department());
                })
                .verifyComplete();
            }

    @Test
    void testGetSubjectFaculty_NotFound() {
        when(registrationApiClient.fetchRegistrationInfo(any(), any())).thenReturn(Mono.just(createSampleRegistrationResponse()));

        Mono<SubjectFacultyResponse> mono = academicService.getSubjectFaculty("INVALID_CODE");

        StepVerifier.create(mono)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
