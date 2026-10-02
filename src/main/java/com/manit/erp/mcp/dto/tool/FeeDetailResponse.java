package com.manit.erp.mcp.dto.tool;

import java.math.BigDecimal;
import java.util.List;

public record FeeDetailResponse(
        BigDecimal totalAmount,
        int matchCount,
        List<FeeItemEntry> feeItems
) {
    public record FeeItemEntry(
            String feeHead,
            Double amount,
            Integer semester,
            String semesterDesc,
            String session
    ) {}
}
