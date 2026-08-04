package com.manit.erp.mcp.clients;

import com.manit.erp.mcp.config.ErpProperties;
import com.manit.erp.mcp.dto.erp.RegistrationErpResponse;
import com.manit.erp.mcp.dto.request.ErpApiRequest;
import com.manit.erp.mcp.exception.ErpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

/**
 * WebClient proxy client for calling the MANIT ERP Registration API (/api/fetch_register).
 */
@Component
public class RegistrationApiClient {

    private static final Logger log = LoggerFactory.getLogger(RegistrationApiClient.class);

    private final WebClient erpWebClient;
    private final ErpProperties erpProperties;

    public RegistrationApiClient(WebClient erpWebClient, ErpProperties erpProperties) {
        this.erpWebClient = erpWebClient;
        this.erpProperties = erpProperties;
    }

    public Mono<List<RegistrationErpResponse>> fetchRegistrationInfo(Integer studentUid, Integer programId) {
        Integer uid = (studentUid != null) ? studentUid : erpProperties.getApi().getDefaultStudentUid();
        Integer pid = (programId != null) ? programId : erpProperties.getApi().getDefaultProgramId();
        ErpApiRequest requestPayload = new ErpApiRequest(uid, pid);

        long startTime = System.currentTimeMillis();

        return erpWebClient.method(HttpMethod.POST)
                .uri(erpProperties.getApi().getRegistrationPath())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + erpProperties.getApi().getAuthToken())
                .bodyValue(requestPayload)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RegistrationErpResponse>>() {})
                .retryWhen(Retry.backoff(
                                erpProperties.getClient().getMaxRetryAttempts(),
                                Duration.ofMillis(erpProperties.getClient().getBackoffPeriodMs()))
                        .filter(throwable -> !(throwable instanceof ErpClientException))
                        .doBeforeRetry(retrySignal -> log.warn("Retrying Registration API call, attempt: {}", retrySignal.totalRetries() + 1)))
                .doOnSuccess(res -> {
                    long latency = System.currentTimeMillis() - startTime;
                    log.info("Successfully fetched ERP Registration data in {} ms", latency);
                })
                .doOnError(err -> log.error("Failed to fetch ERP Registration data: {}", err.getMessage()))
                .onErrorMap(err -> new ErpClientException("ERP Registration API request failed: " + err.getMessage(), err));
    }
}
