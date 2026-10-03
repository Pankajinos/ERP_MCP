package com.manit.erp.mcp.clients;

import com.manit.erp.mcp.config.ErpProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AbstractErpApiClientTest {

    @Test
    void forwardsTheIncomingBearerHeaderToTheErpRequest() {
        AtomicReference<ClientRequest> receivedRequest = new AtomicReference<>();
        ExchangeFunction exchangeFunction = request -> {
            receivedRequest.set(request);
            return Mono.just(ClientResponse.create(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("ok")
                    .build());
        };
        WebClient webClient = WebClient.builder()
                .baseUrl("https://erp.example.test")
                .exchangeFunction(exchangeFunction)
                .build();
        TestErpApiClient client = new TestErpApiClient(webClient, new ErpProperties());
        String authorizationHeader = "Bearer client-token";

        StepVerifier.create(client.call(authorizationHeader))
                .expectNext("ok")
                .verifyComplete();

        assertEquals(authorizationHeader,
                receivedRequest.get().headers().getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    void doesNotMakeAnErpRequestWithoutAuthorization() {
        AtomicReference<ClientRequest> receivedRequest = new AtomicReference<>();
        ExchangeFunction exchangeFunction = request -> {
            receivedRequest.set(request);
            return Mono.error(new AssertionError("ERP request should not be sent"));
        };
        WebClient webClient = WebClient.builder()
                .baseUrl("https://erp.example.test")
                .exchangeFunction(exchangeFunction)
                .build();
        TestErpApiClient client = new TestErpApiClient(webClient, new ErpProperties());

        StepVerifier.create(client.call(" "))
                .expectError(IllegalArgumentException.class)
                .verify();

        assertNull(receivedRequest.get());
    }

    private static class TestErpApiClient extends AbstractErpApiClient {

        private TestErpApiClient(WebClient webClient, ErpProperties erpProperties) {
            super(webClient, erpProperties);
        }

        private Mono<String> call(String authorizationHeader) {
            return executePost("/test", null, null, authorizationHeader, String.class, "Test");
        }
    }
}
