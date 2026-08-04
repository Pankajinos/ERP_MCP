package com.manit.erp.mcp.dto.erp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO matching raw response structure from /api/student_result.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ResultErpResponse(
        @JsonProperty("status") String status,
        @JsonProperty("data") ResultData data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ResultData(
            @JsonProperty("Basic_Details") List<BasicDetail> basicDetails,
            @JsonProperty("Semester_Data") List<SemesterDataItem> semesterData
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BasicDetail(
            @JsonProperty("full_name") String fullName,
            @JsonProperty("program_code") String programCode,
            @JsonProperty("program_name") String programName,
            @JsonProperty("department_name") String departmentName,
            @JsonProperty("roll_no") String rollNo,
            @JsonProperty("registration_no") String registrationNo,
            @JsonProperty("gender") String gender,
            @JsonProperty("institute_email_id") String instituteEmailId,
            @JsonProperty("semester_term_no_id_code") Integer semesterTermNoIdCode,
            @JsonProperty("semester_term_description") String semesterTermDescription,
            @JsonProperty("studentuid") Object studentUid,
            @JsonProperty("degree") String degree
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SemesterDataItem(
            @JsonProperty("status") String status,
            @JsonProperty("msg") String msg,
            @JsonProperty("data") SemesterInnerData data
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SemesterInnerData(
            @JsonProperty("subjects") List<SubjectItem> subjects,
            @JsonProperty("grand_total") GrandTotal grandTotal
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SubjectItem(
            @JsonProperty("subname") String subname,
            @JsonProperty("subject_code") String subjectCode,
            @JsonProperty("total_marks") Double totalMarks,
            @JsonProperty("marks_obtained") Double marksObtained,
            @JsonProperty("mid_term_marks") Double midTermMarks,
            @JsonProperty("end_term_marks") Double endTermMarks,
            @JsonProperty("total_mid_term_marks") Double totalMidTermMarks,
            @JsonProperty("total_end_term_marks") Double totalEndTermMarks,
            @JsonProperty("grade") String grade,
            @JsonProperty("gradePoint") String gradePoint,
            @JsonProperty("credit") String credit,
            @JsonProperty("reg_session") String regSession,
            @JsonProperty("reg_semester_type_id_code") Object regSemesterTypeIdCode,
            @JsonProperty("subject_master_id") Object subjectMasterId,
            @JsonProperty("exam_month") String examMonth,
            @JsonProperty("exam_year") String examYear
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GrandTotal(
            @JsonProperty("total_marks") Double totalMarks,
            @JsonProperty("marks_obtained") Double marksObtained,
            @JsonProperty("percentage") Double percentage,
            @JsonProperty("sgpa") Object sgpa,
            @JsonProperty("total_credits") Double totalCredits,
            @JsonProperty("total_grade_points") Double totalGradePoints,
            @JsonProperty("total_max_grade_points") Double totalMaxGradePoints,
            @JsonProperty("division") String division,
            @JsonProperty("exam_type") String examType,
            @JsonProperty("pass_or_fail") String passOrFail,
            @JsonProperty("reg_session") String regSession
    ) {}
}
