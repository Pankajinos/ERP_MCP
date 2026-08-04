package com.manit.erp.mcp.services;

import com.manit.erp.mcp.dto.tool.FeeInfoResponse;
import reactor.core.publisher.Mono;

/**
 * Service interface for fee structure breakdown.
 */
public interface FeeService {
    Mono<FeeInfoResponse> getFeeInfo(Integer semester, Integer year);
}
