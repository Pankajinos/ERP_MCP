package com.manit.erp.mcp.tools;

import com.manit.erp.mcp.config.McpBearerTokenSupport;
import com.manit.erp.mcp.dto.tool.FeeDetailResponse;
import com.manit.erp.mcp.dto.tool.FeePerSemesterResponse;
import com.manit.erp.mcp.dto.tool.SubjectFacultyResponse;
import com.manit.erp.mcp.dto.tool.SubjectMarksResponse;
import com.manit.erp.mcp.services.AcademicService;
import com.manit.erp.mcp.services.FeeService;
import io.modelcontextprotocol.common.McpTransportContext;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.mcp.McpToolUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcademicToolsTest {

    private static final String AUTHORIZATION_HEADER = "Bearer test-token";

    @Mock
    private FeeService feeService;

    @Mock
    private AcademicService academicService;

    @InjectMocks
    private AcademicTools academicTools;

    @Test
    void testGetFeeDetailsPerSemester_ToolExecution() {
        FeePerSemesterResponse mockResponse = new FeePerSemesterResponse(
                5, 50000.0, 1,
                List.of(new FeePerSemesterResponse.FeeItemDetail("Tuition Fee", 50000.0, "2024-2025", "Sem 5"))
        );
        when(feeService.getFeeDetailsPerSemester(5, AUTHORIZATION_HEADER)).thenReturn(Mono.just(mockResponse));

        FeePerSemesterResponse result = academicTools.getFeeDetailsPerSemester(5, toolContext());

        assertNotNull(result);
        assertEquals(5, result.semester());
        assertEquals(50000.0, result.totalAmount());
        assertEquals(1, result.itemCount());
    }

    @Test
    void testGetFeeDetailPerItem_ToolExecution() {
        FeeDetailResponse mockResponse = new FeeDetailResponse(
                BigDecimal.valueOf(7500.0), 1,
                List.of(new FeeDetailResponse.FeeItemEntry("Hostel Rent", 7500.0, 5, "Sem 5", "2024-2025"))
        );
        when(feeService.getFeeDetailPerItem(
                "Hostel Rent", 5, BigDecimal.valueOf(5000), BigDecimal.valueOf(10000), AUTHORIZATION_HEADER))
                .thenReturn(Mono.just(mockResponse));

        FeeDetailResponse result = academicTools.getFeeDetailPerItem(
                "Hostel Rent", 5, BigDecimal.valueOf(5000), BigDecimal.valueOf(10000), toolContext()
        );

        assertNotNull(result);
        assertEquals(1, result.matchCount());
        assertEquals(BigDecimal.valueOf(7500.0), result.totalAmount());
    }

    @Test
    void testGetSubjectMarksPerSubject_ToolExecution() {
        SubjectMarksResponse mockResponse = new SubjectMarksResponse(
                "MDS323", "Data Mining", 5, 39.0, 39.0, 78.0, 100.0, "A", "8.0", 3.0
        );
        when(academicService.getSubjectMarks("MDS323", AUTHORIZATION_HEADER)).thenReturn(Mono.just(mockResponse));

        SubjectMarksResponse result = academicTools.getSubjectMarksPerSubject("MDS323", toolContext());

        assertNotNull(result);
        assertEquals("MDS323", result.subjectCode());
        assertEquals(78.0, result.marksObtained());
    }

    @Test
    void testGetSubjectFacultyPerSubject_ToolExecution() {
        SubjectFacultyResponse mockResponse = new SubjectFacultyResponse(
                "MDS323", "Data Mining", "Dr. Ali Ahmed", 5, "Computer Science"
        );
        when(academicService.getSubjectFaculty("MDS323", AUTHORIZATION_HEADER)).thenReturn(Mono.just(mockResponse));

        SubjectFacultyResponse result = academicTools.getSubjectFacultyPerSubject("MDS323", toolContext());

        assertNotNull(result);
        assertEquals("MDS323", result.subjectCode());
        assertEquals("Dr. Ali Ahmed", result.facultyName());
    }

    private ToolContext toolContext() {
        McpSyncServerExchange exchange = mock(McpSyncServerExchange.class);
        when(exchange.transportContext()).thenReturn(McpTransportContext.create(
                Map.of(McpBearerTokenSupport.AUTHORIZATION_CONTEXT_KEY, AUTHORIZATION_HEADER)));
        return new ToolContext(Map.of(McpToolUtils.TOOL_CONTEXT_MCP_EXCHANGE_KEY, exchange));
    }
}
