package com.manit.erp.mcp.services.impl;

import com.manit.erp.mcp.clients.FeeApiClient;
import com.manit.erp.mcp.dto.tool.FeeDetailResponse;
import com.manit.erp.mcp.dto.tool.FeePerSemesterResponse;
import com.manit.erp.mcp.mapper.ErpDataMapper;
import com.manit.erp.mcp.services.FeeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Implementation of FeeService providing granular semester and itemized fee operations.
 */
@RequiredArgsConstructor
@Service
public class FeeServiceImpl implements FeeService {

    private static final Logger log = LoggerFactory.getLogger(FeeServiceImpl.class);

    private final FeeApiClient feeApiClient;
    private final ErpDataMapper erpDataMapper;

    @Override
    public Mono<FeePerSemesterResponse> getFeeDetailsPerSemester(int semester, String authorizationHeader) {
        log.info("Processing getFeeDetailsPerSemester for semester: {}", semester);
        if (semester < 0 || semester > 10) {
            return Mono.error(new IllegalArgumentException("Semester must be between 0 and 10."));
        }
        return feeApiClient.fetchStudentFees(null, null, authorizationHeader)
                .map(feeErp -> erpDataMapper.toFeePerSemester(semester, feeErp));
    }

    @Override
    public Mono<FeeDetailResponse> getFeeDetailPerItem(
            String item,
            Integer semester,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String authorizationHeader
    ) {
        log.info("Processing getFeeDetailPerItem with filters - item: {}, semester: {}, minAmount: {}, maxAmount: {}",
                item, semester, minAmount, maxAmount);

        if (semester != null && (semester < 0 || semester > 10)) {
            return Mono.error(new IllegalArgumentException("Semester must be between 0 and 10."));
        }

        return feeApiClient.fetchStudentFees(null, null, authorizationHeader)
                .map(feeErp -> erpDataMapper.toFeeDetailResponse(item, semester, minAmount, maxAmount, feeErp));
    }
}
