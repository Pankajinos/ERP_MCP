package com.manit.erp.mcp.dto.erp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO matching raw response element from /api/fetch_register.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RegistrationErpResponse(
        @JsonProperty("reg_session") Object regSession,
        @JsonProperty("reg_semester_type_id_code") Integer regSemesterTypeIdCode,
        @JsonProperty("current_status") String currentStatus,
        @JsonProperty("creation_time") String creationTime,
        @JsonProperty("semester_term_no_id_code") Integer semesterTermNoIdCode,
        @JsonProperty("feesStatus") String feesStatus,
        @JsonProperty("feesAmount") String feesAmount,
        @JsonProperty("credits") String credits,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("gender") String gender,
        @JsonProperty("roll_no") String rollNo,
        @JsonProperty("depname") String depName,
        @JsonProperty("subjects") List<RegisteredSubject> subjects
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RegisteredSubject(
            @JsonProperty("subject_master_id") Object subjectMasterId,
            @JsonProperty("comp_name_sl_no") Object compNameSlNo,
            @JsonProperty("subject_code") String subjectCode,
            @JsonProperty("subname") String subName,
            @JsonProperty("comp_name") String compName,
            @JsonProperty("code_desc") String codeDesc,
            @JsonProperty("empname") String empName,
            @JsonProperty("empmasId") Object empMasId,
            @JsonProperty("sectionIdCode") Object sectionIdCode,
            @JsonProperty("feedback_is_submitted") Object feedbackIsSubmitted
    ) {}
}
