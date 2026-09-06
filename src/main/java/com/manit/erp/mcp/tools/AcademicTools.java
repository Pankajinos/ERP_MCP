package com.manit.erp.mcp.tools;

import com.manit.erp.mcp.dto.tool.*;
import com.manit.erp.mcp.services.AcademicService;
import com.manit.erp.mcp.services.FeeService;
import com.manit.erp.mcp.services.RegistrationService;
import com.manit.erp.mcp.services.StudentDashboardService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * Spring AI MCP Tool provider class exposing student ERP business capabilities
 * to Claude Desktop and GitHub Copilot Chat.
 */
@Component
@RequiredArgsConstructor
public class AcademicTools {

    private static final Logger log = LoggerFactory.getLogger(AcademicTools.class);

    private final AcademicService academicService;
    private final RegistrationService registrationService;
    private final FeeService feeService;
    private final StudentDashboardService studentDashboardService;

    /**
     * Tool 1: getAcademicSummary
     * Returns an overall academic summary including CGPA, program name, and
     * semester-by-semester SGPA.
     *
     * Example Java Usage:
     *
     * <pre>{@code
     * AcademicSummaryResponse summary = academicTools.getAcademicSummary();
     * }</pre>
     *
     * @return Minimal JSON object containing studentName, program, overall CGPA,
     *         and semester breakdown.
     */
    @Tool(name = "getAcademicSummary", description = "Returns an academic overview of the student including overall CGPA, enrolled program, student full name, and semester-wise breakdown of SGPA and credits earned.")
    public AcademicSummaryResponse getAcademicSummary() {
        log.info("MCP Tool Executed: getAcademicSummary");
        long start = System.currentTimeMillis();
        try {
            return academicService.getAcademicSummary().block();
        } finally {
            log.info("MCP Tool Finished: getAcademicSummary in {} ms", System.currentTimeMillis() - start);
        }
    }

    /**
     * Tool 2: getSemesterDetails
     * Returns comprehensive details for a targeted semester combining results,
     * registered subjects with faculty, and fees.
     *
     * Example Java Usage:
     *
     * <pre>{@code
     * SemesterDetailsResponse details = academicTools.getSemesterDetails(5);
     * }</pre>
     *
     * @param semester Target semester term number (e.g. 1, 2, 5, 8).
     * @return Merged semester details object with SGPA, credits, subject list
     *         (code, name, faculty), and grouped fees.
     */
    @Tool(name = "getSemesterDetails", description = "Returns complete information for a target semester including SGPA, credits earned, subject details with assigned faculty names, and semester fee summary.")
    public SemesterDetailsResponse getSemesterDetails(
            @ToolParam(description = "Target semester term number (e.g., 1, 2, 5, 8)") int semester) {
        log.info("MCP Tool Executed: getSemesterDetails for semester: {}", semester);
        long start = System.currentTimeMillis();
        try {
            return academicService.getSemesterDetails(semester).block();
        } finally {
            log.info("MCP Tool Finished: getSemesterDetails in {} ms", System.currentTimeMillis() - start);
        }
    }

    /**
     * Tool 3: getSubjectDetails
     * Returns detailed subject information including faculty, midterm marks,
     * endterm marks, total marks, grade, and credits.
     *
     * Example Java Usage:
     *
     * <pre>{@code
     * SubjectDetailsResponse subject = academicTools.getSubjectDetails("MDS316");
     * }</pre>
     *
     * @param subjectCode Unique course code (e.g. 'MDS316', 'MDS323').
     * @return Detailed subject object.
     */
    @Tool(name = "getSubjectDetails", description = "Returns complete information for a given subject code (e.g., 'MDS316', 'MDS323') including course title, faculty name, midterm marks, endterm marks, total marks, grade, and credits.")
    public SubjectDetailsResponse getSubjectDetails(
            @ToolParam(description = "Subject course code (e.g., 'MDS316', 'MDS323')") String subjectCode) {
        log.info("MCP Tool Executed: getSubjectDetails for subjectCode: {}", subjectCode);
        long start = System.currentTimeMillis();
        try {
            return academicService.getSubjectDetails(subjectCode).block();
        } finally {
            log.info("MCP Tool Finished: getSubjectDetails in {} ms", System.currentTimeMillis() - start);
        }
    }

    /**
     * Tool 4: getRegistrationInfo
     * Returns registration info for a specified semester or defaults to current
     * active semester.
     *
     * Example Java Usage:
     *
     * <pre>{@code
     * RegistrationInfoResponse regInfo = academicTools.getRegistrationInfo(5);
     * }</pre>
     *
     * @param semester Optional semester number. If omitted or null, returns current
     *                 semester registration.
     * @return Course registration status, registered subjects, assigned faculty,
     *         and feedback pending status.
     */
    @Tool(name = "getRegistrationInfo", description = "Returns course registration details for a specific semester or the current active semester if unsupplied. Includes total credits, registered subjects, assigned faculty, and feedback submission status.")
    public RegistrationInfoResponse getRegistrationInfo(
            @ToolParam(description = "Optional semester term number. If omitted, returns current semester registration.") Integer semester) {
        log.info("MCP Tool Executed: getRegistrationInfo for semester: {}", semester);
        long start = System.currentTimeMillis();
        try {
            return registrationService.getRegistrationInfo(semester).block();
        } finally {
            log.info("MCP Tool Finished: getRegistrationInfo in {} ms", System.currentTimeMillis() - start);
        }
    }

    /**
     * Tool 5: getFeeInfo
     * Returns semester-wise fee information broken down into Academic Fee, Hostel
     * Fee, and Other Fee.
     *
     * Example Java Usage:
     *
     * <pre>{@code
     * FeeInfoResponse feeInfo = academicTools.getFeeInfo(5, 2025);
     * }</pre>
     *
     * @param semester Target semester term number.
     * @param year     Optional academic year (e.g. 2025).
     * @return Categorized fee response with total and itemized breakdown.
     */
    @Tool(name = "getFeeInfo", description = "Returns semester-wise fee breakdown aggregated into Academic Fee, Hostel Fee, and Other Fee, along with total amount and individual line items.")
    public FeeInfoResponse getFeeInfo(
            @ToolParam(description = "Target semester term number (e.g. 5)") Integer semester,
            @ToolParam(description = "Optional academic year (e.g. 2025)") Integer year) {
        log.info("MCP Tool Executed: getFeeInfo for semester: {}, year: {}", semester, year);
        long start = System.currentTimeMillis();
        try {
            return feeService.getFeeInfo(semester, year).block();
        } finally {
            log.info("MCP Tool Finished: getFeeInfo in {} ms", System.currentTimeMillis() - start);
        }
    }

    /**
     * Tool 6: searchAcademicRecords
     * Searches student academic records across subjects, grades, marks, labs, and
     * credit values matching the query.
     *
     * Example Java Usage:
     *
     * <pre>{@code
     * AcademicSearchResultResponse searchResults = academicTools.searchAcademicRecords("A grade");
     * }</pre>
     *
     * @param query Search query string (e.g., 'A grade', 'marks above 90', 'labs',
     *              '4 credit', 'Programming').
     * @return Filtered array of matching academic records.
     */
    @Tool(name = "searchAcademicRecords", description = "Searches academic records for matching subjects, grades, labs, or credit counts. Query examples: 'A grade', 'marks above 90', 'labs', '4 credit', 'Programming'.")
    public AcademicSearchResultResponse searchAcademicRecords(
            @ToolParam(description = "Search filter query (e.g. 'A grade', 'labs', 'Programming', '4 credit')") String query) {
        log.info("MCP Tool Executed: searchAcademicRecords with query: {}", query);
        long start = System.currentTimeMillis();
        try {
            return academicService.searchAcademicRecords(query).block();
        } finally {
            log.info("MCP Tool Finished: searchAcademicRecords in {} ms", System.currentTimeMillis() - start);
        }
    }

    /**
     * Tool 7: getStudentDashboard
     * Returns top-level executive student dashboard summarizing student metrics.
     *
     * Example Java Usage:
     *
     * <pre>{@code
     * StudentDashboardResponse dashboard = academicTools.getStudentDashboard();
     * }</pre>
     *
     * @return Top-level student metrics summary.
     */
    @Tool(name = "getStudentDashboard", description = "Returns top-level executive student dashboard summarizing student name, active current semester, cumulative CGPA, total earned credits, backlogs count, and total pending fees.")
    public StudentDashboardResponse getStudentDashboard() {
        log.info("MCP Tool Executed: getStudentDashboard");
        long start = System.currentTimeMillis();
        try {
            return studentDashboardService.getStudentDashboard().block();
        } finally {
            log.info("MCP Tool Finished: getStudentDashboard in {} ms", System.currentTimeMillis() - start);
        }
    }
}
