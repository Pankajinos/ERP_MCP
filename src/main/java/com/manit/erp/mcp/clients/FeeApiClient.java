package com.manit.erp.mcp.clients;

import com.manit.erp.mcp.config.ErpProperties;
import com.manit.erp.mcp.dto.erp.FeeErpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * WebClient proxy client for calling the MANIT ERP Fee API (/api/student_fees).
 */
@Component
public class FeeApiClient extends AbstractErpApiClient {

    public FeeApiClient(WebClient erpWebClient, ErpProperties erpProperties) {
        super(erpWebClient, erpProperties);
    }

    public Mono<FeeErpResponse> fetchStudentFees(Integer studentUid, Integer programId, String authorizationHeader) {
        return executePost(
                erpProperties.getApi().getFeePath(),
                studentUid,
                programId,
                authorizationHeader,
                FeeErpResponse.class,
                "Fee"
        );
    }
}
