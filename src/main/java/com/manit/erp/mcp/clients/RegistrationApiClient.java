package com.manit.erp.mcp.clients;

import com.manit.erp.mcp.config.ErpProperties;
import com.manit.erp.mcp.dto.erp.RegistrationErpResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * WebClient proxy client for calling the MANIT ERP Registration API (/api/fetch_register).
 */
@Component
public class RegistrationApiClient extends AbstractErpApiClient {

    public RegistrationApiClient(WebClient erpWebClient, ErpProperties erpProperties) {
        super(erpWebClient, erpProperties);
    }

    ///api/fetch_register returns a list of RegistrationErpResponse
    public Mono<List<RegistrationErpResponse>> fetchRegistrationInfo(Integer studentUid, Integer programId) {
        return executePost(
                erpProperties.getApi().getRegistrationPath(),
                studentUid,
                programId,
                new ParameterizedTypeReference<List<RegistrationErpResponse>>() {},
                "Registration"
        );
    }
}
