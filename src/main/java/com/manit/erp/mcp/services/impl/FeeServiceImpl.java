package com.manit.erp.mcp.services.impl;

import com.manit.erp.mcp.clients.FeeApiClient;
import com.manit.erp.mcp.dto.tool.FeeInfoResponse;
import com.manit.erp.mcp.mapper.ErpDataMapper;
import com.manit.erp.mcp.services.FeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of FeeService for fee categorization and semester fee breakdown.
 */
@Service
public class FeeServiceImpl implements FeeService {

    private static final Logger log = LoggerFactory.getLogger(FeeServiceImpl.class);

    private final FeeApiClient feeApiClient;
    private final ErpDataMapper erpDataMapper;

    public FeeServiceImpl(FeeApiClient feeApiClient, ErpDataMapper erpDataMapper) {
        this.feeApiClient = feeApiClient;
        this.erpDataMapper = erpDataMapper;
    }

    @Override
    public Mono<FeeInfoResponse> getFeeInfo(Integer semester, Integer year) {
        log.info("Processing getFeeInfo service request for semester: {}, year: {}", semester, year);
        return feeApiClient.fetchStudentFees(null, null)
                .map(feeErp -> erpDataMapper.toFeeInfo(semester, year, feeErp));
    }
}
