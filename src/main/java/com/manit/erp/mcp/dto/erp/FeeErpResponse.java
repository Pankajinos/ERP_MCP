package com.manit.erp.mcp.dto.erp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * DTO matching raw response structure from /api/student_fees.
 * Supports both { "feeData": [...] }, { "data": [...] }, and direct [ ... ] list responses.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = FeeErpResponseDeserializer.class)
public record FeeErpResponse(
        @JsonProperty("feeData") List<FeeItem> feeData,
        @JsonProperty("data") List<FeeItem> data
) {
    public FeeErpResponse(List<FeeItem> feeData) {
        this(feeData, feeData);
    }

    public List<FeeItem> allItems() {
        if (feeData != null && !feeData.isEmpty()) {
            return feeData;
        }
        if (data != null && !data.isEmpty()) {
            return data;
        }
        return List.of();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FeeItem(
            @JsonProperty("institute_id") Object instituteId,
            @JsonProperty("student_demand_id") Object studentDemandId,
            @JsonProperty("studentuid") Object studentUid,
            @JsonProperty("fees_sub_head_id") Object feesSubHeadId,
            @JsonProperty("fees_session") Object feesSession,
            @JsonProperty("semester_type_id_code") Object semesterTypeIdCode,
            @JsonProperty("semester_term_no_id_code") Object semesterTermNoIdCode,
            @JsonProperty("semester_no") Object semesterNo,
            @JsonProperty("semester") Object semester,
            @JsonProperty("fees_price") Object feesPrice,
            @JsonProperty("fees_sub_head_title") String feesSubHeadTitle,
            @JsonProperty("fees_head_title") String feesHeadTitle,
            @JsonProperty("fees_subhead_title") String feesSubheadTitle,
            @JsonProperty("sub_head_title") String subHeadTitle,
            @JsonProperty("fees_head_id") Object feesHeadId,
            @JsonProperty("semester_code_desc") String semesterCodeDesc,
            @JsonProperty("amount") Object amount,
            @JsonProperty("created_at") String createdAt
    ) {
        public FeeItem(
                Object instituteId,
                Object studentDemandId,
                Object studentUid,
                Object feesSubHeadId,
                Object feesSession,
                Object semesterTypeIdCode,
                Object feesPrice,
                String feesSubHeadTitle,
                Object feesHeadId,
                String semesterCodeDesc,
                Object amount,
                String createdAt
        ) {
            this(
                    instituteId,
                    studentDemandId,
                    studentUid,
                    feesSubHeadId,
                    feesSession,
                    semesterTypeIdCode,
                    null,
                    null,
                    null,
                    feesPrice,
                    feesSubHeadTitle,
                    null,
                    null,
                    null,
                    feesHeadId,
                    semesterCodeDesc,
                    amount,
                    createdAt
            );
        }
    }
}
