package com.manit.erp.mcp.mapper;

import com.manit.erp.mcp.dto.erp.FeeErpResponse;
import com.manit.erp.mcp.dto.erp.RegistrationErpResponse;
import com.manit.erp.mcp.dto.erp.ResultErpResponse;
import com.manit.erp.mcp.dto.tool.FeeDetailResponse;
import com.manit.erp.mcp.dto.tool.FeePerSemesterResponse;
import com.manit.erp.mcp.dto.tool.SubjectFacultyResponse;
import com.manit.erp.mcp.dto.tool.SubjectMarksResponse;
import com.manit.erp.mcp.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErpDataMapperTest {

    private ErpDataMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ErpDataMapper();
    }

    @Test
    void testToFeePerSemester_MatchingSemester() {
        FeeErpResponse.FeeItem item1 = new FeeErpResponse.FeeItem(
                1, 1, 101, 1, "2024-2025", 5, 40000.0, "Tuition Fee", 1, "Sem 5", 40000.0, "2025-01-01"
        );
        FeeErpResponse.FeeItem item2 = new FeeErpResponse.FeeItem(
                1, 1, 101, 1, "2024-2025", 4, 35000.0, "Tuition Fee", 1, "Sem 4", 35000.0, "2024-01-01"
        );
        FeeErpResponse feeErp = new FeeErpResponse(List.of(item1, item2));

        FeePerSemesterResponse response = mapper.toFeePerSemester(5, feeErp);

        assertEquals(5, response.semester());
        assertEquals(40000.0, response.totalAmount());
        assertEquals(1, response.itemCount());
        assertEquals("Tuition Fee", response.items().get(0).feeHead());
    }

    @Test
    void testToFeeDetailResponse_WithFilters() {
        FeeErpResponse.FeeItem item1 = new FeeErpResponse.FeeItem(
                1, 1, 101, 1, "2024-2025", 5, 40000.0, "Tuition Fee", 1, "Sem 5", 40000.0, "2025-01-01"
        );
        FeeErpResponse.FeeItem item2 = new FeeErpResponse.FeeItem(
                1, 1, 101, 1, "2024-2025", 5, 7500.0, "Hostel Rent", 1, "Sem 5", 7500.0, "2025-01-01"
        );
        FeeErpResponse feeErp = new FeeErpResponse(List.of(item1, item2));

        FeeDetailResponse response = mapper.toFeeDetailResponse(
                "Hostel", 5, BigDecimal.valueOf(5000), BigDecimal.valueOf(10000), feeErp
        );

        assertEquals(1, response.matchCount());
        assertEquals(new BigDecimal("7500.00"), response.totalAmount());
        assertEquals("Hostel Rent", response.feeItems().get(0).feeHead());
    }

    @Test
    void testToSubjectMarks_Success() {
        ResultErpResponse.SubjectItem s1 = new ResultErpResponse.SubjectItem(
                "Data Mining", "MDS323", 100.0, 78.0, 39.0, 39.0, 40.0, 40.0,
                "A", "8.0", "3.0", "2024-2025", 5, 101, "Dec", "2024"
        );
        ResultErpResponse.SemesterInnerData inner = new ResultErpResponse.SemesterInnerData(List.of(s1), null);
        ResultErpResponse.SemesterDataItem semItem = new ResultErpResponse.SemesterDataItem("SUCCESS", "OK", inner);
        ResultErpResponse.SemesterDataItem emptySem = new ResultErpResponse.SemesterDataItem("SUCCESS", "OK", new ResultErpResponse.SemesterInnerData(List.of(), null));
        ResultErpResponse resultErp = new ResultErpResponse("SUCCESS", new ResultErpResponse.ResultData(List.of(), List.of(emptySem, emptySem, emptySem, emptySem, semItem)));

        SubjectMarksResponse response = mapper.toSubjectMarks("MDS323", resultErp);

        assertEquals("MDS323", response.subjectCode());
        assertEquals("Data Mining", response.subjectName());
        assertEquals(5, response.semester());
        assertEquals(78.0, response.marksObtained());
    }

    @Test
    void testToSubjectMarks_NotFound() {
        ResultErpResponse resultErp = new ResultErpResponse("SUCCESS", new ResultErpResponse.ResultData(List.of(), List.of()));

        assertThrows(ResourceNotFoundException.class, () -> mapper.toSubjectMarks("UNKNOWN", resultErp));
    }

    @Test
    void testToSubjectFaculty_Success() {
        RegistrationErpResponse.RegisteredSubject rs1 = new RegistrationErpResponse.RegisteredSubject(
                101, 1, "MDS323", "Data Mining", "MDS323", "DESC", "Dr. Ali Ahmed", 55, 1, true
        );
        RegistrationErpResponse reg = new RegistrationErpResponse(
                "2024-2025", 5, "ACTIVE", "2024-08-01", 5, "PAID", "50000", "21.0",
                "Pankaj Soni", "M", "12345", "Computer Science", List.of(rs1)
        );

        SubjectFacultyResponse response = mapper.toSubjectFaculty("MDS323", List.of(reg));

        assertEquals("MDS323", response.subjectCode());
        assertEquals("Data Mining", response.subjectName());
        assertEquals("Dr. Ali Ahmed", response.facultyName());
        assertEquals(5, response.semester());
        assertEquals("Computer Science", response.department());
    }
}
