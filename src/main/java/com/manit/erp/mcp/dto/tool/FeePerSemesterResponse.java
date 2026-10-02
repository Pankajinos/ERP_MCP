package com.manit.erp.mcp.dto.tool;

import java.util.List;

public record FeePerSemesterResponse(
        int semester,
        double totalAmount,
        int itemCount,
        List<FeeItemDetail> items
) {
    public record FeeItemDetail(
            String feeHead,
            double amount,
            String session,
            String semesterDesc
    ) {}
}
