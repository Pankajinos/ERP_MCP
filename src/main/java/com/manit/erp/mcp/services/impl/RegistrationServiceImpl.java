package com.manit.erp.mcp.services.impl;

import com.manit.erp.mcp.clients.RegistrationApiClient;
import com.manit.erp.mcp.dto.tool.RegistrationInfoResponse;
import com.manit.erp.mcp.mapper.ErpDataMapper;
import com.manit.erp.mcp.services.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of RegistrationService handling registered courses and feedback verification.
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    private final RegistrationApiClient registrationApiClient;
    private final ErpDataMapper erpDataMapper;

    public RegistrationServiceImpl(RegistrationApiClient registrationApiClient, ErpDataMapper erpDataMapper) {
        this.registrationApiClient = registrationApiClient;
        this.erpDataMapper = erpDataMapper;
    }

    @Override
    public Mono<RegistrationInfoResponse> getRegistrationInfo(Integer semester) {
        log.info("Processing getRegistrationInfo service request for semester: {}", semester);
        return registrationApiClient.fetchRegistrationInfo(null, null)
                .map(regList -> erpDataMapper.toRegistrationInfo(semester, regList));
    }
}
