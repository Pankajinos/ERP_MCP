package com.manit.erp.mcp.clients;

import com.manit.erp.mcp.config.ErpProperties;
import com.manit.erp.mcp.dto.erp.ResultErpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * WebClient proxy client for calling the MANIT ERP Result API (/api/student_result).
 */
@Component
public class ResultApiClient extends AbstractErpApiClient {

    public ResultApiClient(WebClient erpWebClient, ErpProperties erpProperties) {
        super(erpWebClient, erpProperties);
    }

    public Mono<ResultErpResponse> fetchStudentResult(Integer studentUid, Integer programId, String authorizationHeader) {
        return executePost(
                erpProperties.getApi().getResultPath(),
                studentUid,
                programId,
                authorizationHeader,
                ResultErpResponse.class,
                "Result"
        );
    }
}
