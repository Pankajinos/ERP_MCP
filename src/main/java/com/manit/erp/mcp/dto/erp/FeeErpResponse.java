package com.manit.erp.mcp.dto.erp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO matching raw response structure from /api/student_fees.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FeeErpResponse(
        @JsonProperty("feeData") List<FeeItem> feeData
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FeeItem(
            @JsonProperty("institute_id") Integer instituteId,
            @JsonProperty("student_demand_id") Object studentDemandId,
            @JsonProperty("studentuid") Object studentUid,
            @JsonProperty("fees_sub_head_id") Object feesSubHeadId,
            @JsonProperty("fees_session") Object feesSession,
            @JsonProperty("semester_type_id_code") Integer semesterTypeIdCode,
            @JsonProperty("fees_price") Double feesPrice,
            @JsonProperty("fees_sub_head_title") String feesSubHeadTitle,
            @JsonProperty("fees_head_id") Object feesHeadId,
            @JsonProperty("semester_code_desc") String semesterCodeDesc,
            @JsonProperty("amount") Double amount,
            @JsonProperty("created_at") String createdAt
    ) {}
}
