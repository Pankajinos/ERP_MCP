package com.manit.erp.mcp.tools;

import com.manit.erp.mcp.dto.tool.AcademicSearchResultResponse;
import com.manit.erp.mcp.dto.tool.AcademicSummaryResponse;
import com.manit.erp.mcp.dto.tool.FeeInfoResponse;
import com.manit.erp.mcp.dto.tool.RegistrationInfoResponse;
import com.manit.erp.mcp.dto.tool.SemesterDetailsResponse;
import com.manit.erp.mcp.dto.tool.StudentDashboardResponse;
import com.manit.erp.mcp.dto.tool.SubjectDetailsResponse;
import com.manit.erp.mcp.services.AcademicService;
import com.manit.erp.mcp.services.FeeService;
import com.manit.erp.mcp.services.RegistrationService;
import com.manit.erp.mcp.services.StudentDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AcademicTools {

    private final AcademicService academicService;
    private final RegistrationService registrationService;
    private final FeeService feeService;
    private final StudentDashboardService studentDashboardService;

    @Tool(
            name = "getAcademicSummary",
            description = """
            Returns an academic overview of the authenticated student,
            including full name, program, overall CGPA, and semester-wise
            SGPA and credits earned.
            """
    )
    public AcademicSummaryResponse getAcademicSummary() {
        return academicService.getAcademicSummary().block();
    }

    @Tool(
            name = "getSemesterDetails",
            description = """
            Returns complete academic information for a specific semester,
            including SGPA, credits earned, subjects, assigned faculty,
            and semester fee information.
            """
    )
    public SemesterDetailsResponse getSemesterDetails(
            @ToolParam(description = "Semester number, for example 1, 2, 5, or 8")
            int semester) {

        return academicService.getSemesterDetails(semester).block();
    }

    @Tool(
            name = "getSubjectDetails",
            description = """
            Returns detailed information about a specific subject,
            including course title, faculty, marks, grade, and credits.
            """
    )
    public SubjectDetailsResponse getSubjectDetails(
            @ToolParam(description = "Subject course code, for example MDS316")
            String subjectCode) {

        return academicService.getSubjectDetails(subjectCode).block();
    }

    @Tool(
            name = "getRegistrationInfo",
            description = """
            Returns course registration information for a semester.
            If no semester is provided, returns information for the
            current active semester.
            """
    )
    public RegistrationInfoResponse getRegistrationInfo(
            @ToolParam(description = "Optional semester number. Omit to use the current active semester.")
            Integer semester) {

        return registrationService.getRegistrationInfo(semester).block();
    }

    @Tool(
            name = "getFeeInfo",
            description = """
            Returns fee information for a semester, including academic,
            hostel, and other fees, along with totals and individual
            fee items.
            """
    )
    public FeeInfoResponse getFeeInfo(
            @ToolParam(description = "Semester number")
            Integer semester,

            @ToolParam(description = "Optional academic year, for example 2025")
            Integer year) {

        return feeService.getFeeInfo(semester, year).block();
    }

    @Tool(
            name = "searchAcademicRecords",
            description = """
            Searches the authenticated student's academic records using
            natural-language criteria such as subject name, grade,
            marks, labs, or credit count.
            """
    )
    public AcademicSearchResultResponse searchAcademicRecords(
            @ToolParam(description = "Natural-language search query")
            String query) {

        return academicService.searchAcademicRecords(query).block();
    }

    @Tool(
            name = "getStudentDashboard",
            description = """
            Returns a high-level dashboard for the authenticated student,
            including current semester, CGPA, earned credits, backlog count,
            and pending fees.
            """
    )
    public StudentDashboardResponse getStudentDashboard() {
        return studentDashboardService.getStudentDashboard().block();
    }
}