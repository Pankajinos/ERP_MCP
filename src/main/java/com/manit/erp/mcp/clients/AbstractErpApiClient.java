package com.manit.erp.mcp.clients;

import com.manit.erp.mcp.config.ErpProperties;
import com.manit.erp.mcp.dto.request.ErpApiRequest;
import com.manit.erp.mcp.exception.ErpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Function;

/**
 * Abstract base client providing common WebClient POST request execution,
 * retries, latency logging, and error handling for ERP APIs.
 */
public abstract class AbstractErpApiClient {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final WebClient erpWebClient;
    protected final ErpProperties erpProperties;

    protected AbstractErpApiClient(WebClient erpWebClient, ErpProperties erpProperties) {
        this.erpWebClient = erpWebClient;
        this.erpProperties = erpProperties;
    }

    /**
     * Executes a POST request to an ERP endpoint expecting a single object response.
     */
    protected <T> Mono<T> executePost(
            String uriPath,
            Integer studentUid,
            Integer programId,
            String authorizationHeader,
            Class<T> responseClass,
            String apiName
    ) {
        return executePostInternal(uriPath, studentUid, programId, authorizationHeader,
                spec -> spec.bodyToMono(responseClass), apiName);
    }

    /**
     * Executes a POST request to an ERP endpoint expecting a generic or parameterized response (e.g., List<T>).
     */
    protected <T> Mono<T> executePost(
            String uriPath,
            Integer studentUid,
            Integer programId,
            String authorizationHeader,
            ParameterizedTypeReference<T> responseType,
            String apiName
    ) {
        return executePostInternal(uriPath, studentUid, programId, authorizationHeader,
                spec -> spec.bodyToMono(responseType), apiName);
    }

    private <T> Mono<T> executePostInternal(
            String uriPath,
            Integer studentUid,
            Integer programId,
            String authorizationHeader,
            Function<WebClient.ResponseSpec, Mono<T>> bodyExtractor,
            String apiName
    ) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return Mono.error(new IllegalArgumentException("A Bearer Authorization header is required"));
        }

        Integer uid = (studentUid != null) ? studentUid : erpProperties.getApi().getDefaultStudentUid();
        Integer pid = (programId != null) ? programId : erpProperties.getApi().getDefaultProgramId();
        ErpApiRequest requestPayload = new ErpApiRequest(uid, pid);

        long startTime = System.currentTimeMillis();

        return bodyExtractor.apply(
                erpWebClient.method(HttpMethod.POST)
                        .uri(uriPath)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .bodyValue(requestPayload)
                        .retrieve()
        )
        .retryWhen(Retry.backoff(
                        erpProperties.getClient().getMaxRetryAttempts(),
                        Duration.ofMillis(erpProperties.getClient().getBackoffPeriodMs()))
                .filter(throwable -> !(throwable instanceof WebClientResponseException wcre && wcre.getStatusCode().is4xxClientError())
                        && !(throwable instanceof ErpClientException))
                .doBeforeRetry(retrySignal -> log.warn("Retrying {} API call, attempt: {}", apiName, retrySignal.totalRetries() + 1)))
        .doOnSuccess(res -> {
            long latency = System.currentTimeMillis() - startTime;
            log.info("Successfully fetched ERP {} data in {} ms", apiName, latency);
        })
        .doOnError(err -> log.error("Failed to fetch ERP {} data: {}", apiName, err.getMessage()))
        .onErrorMap(err -> new ErpClientException("ERP " + apiName + " API request failed: " + err.getMessage(), err));
    }
}
