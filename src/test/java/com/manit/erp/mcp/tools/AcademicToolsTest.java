package com.manit.erp.mcp.tools;

import com.manit.erp.mcp.dto.tool.AcademicSummaryResponse;
import com.manit.erp.mcp.dto.tool.SemesterDetailsResponse;
import com.manit.erp.mcp.dto.tool.StudentDashboardResponse;
import com.manit.erp.mcp.services.AcademicService;
import com.manit.erp.mcp.services.FeeService;
import com.manit.erp.mcp.services.RegistrationService;
import com.manit.erp.mcp.services.StudentDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcademicToolsTest {

    @Mock
    private AcademicService academicService;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private FeeService feeService;

    @Mock
    private StudentDashboardService studentDashboardService;

    private AcademicTools academicTools;

    @BeforeEach
    void setUp() {
        academicTools = new AcademicTools(academicService, registrationService, feeService, studentDashboardService);
    }

    @Test
    void testGetAcademicSummary_ToolExecution() {
        AcademicSummaryResponse mockResponse = new AcademicSummaryResponse("Pankaj Soni", "M.Tech CSE", 8.73, List.of());
        when(academicService.getAcademicSummary()).thenReturn(Mono.just(mockResponse));

        AcademicSummaryResponse result = academicTools.getAcademicSummary();

        assertNotNull(result);
        assertEquals("Pankaj Soni", result.studentName());
        assertEquals(8.73, result.cgpa());
    }

    @Test
    void testGetSemesterDetails_ToolExecution() {
        SemesterDetailsResponse mockResponse = new SemesterDetailsResponse(5, 8.91, 21.0, List.of(), new SemesterDetailsResponse.FeeSummary(42000.0, 7500.0, 2200.0));
        when(academicService.getSemesterDetails(5)).thenReturn(Mono.just(mockResponse));

        SemesterDetailsResponse result = academicTools.getSemesterDetails(5);

        assertNotNull(result);
        assertEquals(5, result.semester());
        assertEquals(8.91, result.sgpa());
    }

    @Test
    void testGetStudentDashboard_ToolExecution() {
        StudentDashboardResponse mockDashboard = new StudentDashboardResponse("Pankaj Soni", 5, 8.73, 98.0, 0, 7500.0);
        when(studentDashboardService.getStudentDashboard()).thenReturn(Mono.just(mockDashboard));

        StudentDashboardResponse result = academicTools.getStudentDashboard();

        assertNotNull(result);
        assertEquals("Pankaj Soni", result.student());
        assertEquals(5, result.currentSemester());
        assertEquals(8.73, result.cgpa());
    }
}
