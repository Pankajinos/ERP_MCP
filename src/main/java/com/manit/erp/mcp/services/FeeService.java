package com.manit.erp.mcp.services;

import com.manit.erp.mcp.dto.tool.FeeDetailResponse;
import com.manit.erp.mcp.dto.tool.FeePerSemesterResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface FeeService {

    Mono<FeePerSemesterResponse> getFeeDetailsPerSemester(int semester);

    Mono<FeeDetailResponse> getFeeDetailPerItem(
            String item,
            Integer semester,
            BigDecimal minAmount,
            BigDecimal maxAmount
    );
}
