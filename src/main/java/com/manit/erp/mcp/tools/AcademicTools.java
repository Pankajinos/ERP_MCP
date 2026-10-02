package com.manit.erp.mcp.tools;

import com.manit.erp.mcp.dto.tool.FeeDetailResponse;
import com.manit.erp.mcp.dto.tool.FeePerSemesterResponse;
import com.manit.erp.mcp.dto.tool.SubjectFacultyResponse;
import com.manit.erp.mcp.dto.tool.SubjectMarksResponse;
import com.manit.erp.mcp.services.AcademicService;
import com.manit.erp.mcp.services.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class AcademicTools {

    private final FeeService feeService;
    private final AcademicService academicService;

    @Tool(
            name = "getFeeDetailsPerSemester",
            description = """
            Returns complete fee details and line-item breakdown for a specific semester,
            including semester number, total fee amount, and individual fee heads.
            """
    )
    public FeePerSemesterResponse getFeeDetailsPerSemester(
            @ToolParam(description = "Semester number, for example 1, 2, 5")
            int semester) {
        return feeService.getFeeDetailsPerSemester(semester).block();
    }

    @Tool(
            name = "getFeeDetailPerItem",
            description = """
            Retrieves student fee details using optional filters.

            All parameters are optional. Multiple filters can be combined.
            If no filters are provided, returns all available fee details.

            item:
            Optional fee item name to filter by. The value should match one of the
            available fee item names, such as:
            Tuition Fee, Caution Money, OT-Academic Fee, OT-Alumini Fee,
            OT-Student Training & Placement, OT-Convocation Fee,
            SF-Poor Students Fund, SF-Student Medical Fund,
            SF-Institute Development Fund, SF-Student Activity Fee,
            SF-Library Fee, SF-Registration & Examination Fee,
            SF-Central Computing Facility & Internet Fee,
            SF-Membership Fee for NOSP/NASA,
            Registration & Medical Examination Fee(OT), Bus Fees,
            Caution Money(Hostel), Hostel Maintenance Charges, Hostel Rent.

            If the user's wording differs from the exact item name, map it to the
            closest matching fee item.

            semester:
            Optional semester number. Must be an integer between 0 and 10.
            For example, semester=5 refers to Semester 5.

            minAmount:
            Optional minimum fee amount. Returns fee items whose amount is greater
            than or equal to this value.

            maxAmount:
            Optional maximum fee amount. Returns fee items whose amount is less than
            or equal to this value.

            Examples:
            - "Show my hostel rent" -> item="Hostel Rent"
            - "Show library fee for semester 5" -> item="SF-Library Fee", semester=5
            - "Show fees above 5000" -> minAmount=5000
            - "Show fees below 2000" -> maxAmount=2000
            - "Show fees between 1000 and 5000" -> minAmount=1000, maxAmount=5000
            - "Show all fees for semester 5" -> semester=5
            """
    )
    public FeeDetailResponse getFeeDetailPerItem(
            @ToolParam(description = "Optional fee item name or keyword (e.g. Tuition Fee, Hostel Rent, SF-Library Fee)")
            String item,

            @ToolParam(description = "Optional semester number between 0 and 10")
            Integer semester,

            @ToolParam(description = "Optional minimum fee amount threshold")
            BigDecimal minAmount,

            @ToolParam(description = "Optional maximum fee amount threshold")
            BigDecimal maxAmount
    ) {
        return feeService.getFeeDetailPerItem(item, semester, minAmount, maxAmount).block();
    }

    @Tool(
            name = "getSubjectMarksPerSubject",
            description = """
            Returns detailed examination marks, score breakdown (midterm, endterm, total),
            and grade for a specific subject by subject code or subject name.
            """
    )
    public SubjectMarksResponse getSubjectMarksPerSubject(
            @ToolParam(description = "Subject code (e.g., MDS316, MDS323) or subject name (e.g., Data Mining)")
            String subject) {
        return academicService.getSubjectMarks(subject).block();
    }

    @Tool(
            name = "getSubjectFacultyPerSubject",
            description = """
            Returns assigned faculty instructor and course details for a specific subject
            by subject code or subject name.
            """
    )
    public SubjectFacultyResponse getSubjectFacultyPerSubject(
            @ToolParam(description = "Subject code (e.g., MDS316, MDS323) or subject name (e.g., Data Mining)")
            String subject) {
        return academicService.getSubjectFaculty(subject).block();
    }
}
