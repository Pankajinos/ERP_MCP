package com.manit.erp.mcp.services;

import com.manit.erp.mcp.dto.tool.RegistrationInfoResponse;
import reactor.core.publisher.Mono;

/**
 * Service interface for course registration details and feedback status.
 */
public interface RegistrationService {
    Mono<RegistrationInfoResponse> getRegistrationInfo(Integer semester);
}
