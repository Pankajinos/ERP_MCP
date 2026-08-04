package com.manit.erp.mcp.dto.tool;

import java.util.List;

/**
 * Tool response DTO for getFeeInfo(semester, year).
 */
public record FeeInfoResponse(
        Integer semester,
        Integer year,
        Double academicFee,
        Double hostelFee,
        Double otherFee,
        Double total,
        List<FeeBreakdownItem> breakdown
) {
    public record FeeBreakdownItem(
            String head,
            Double amount
    ) {}
}
