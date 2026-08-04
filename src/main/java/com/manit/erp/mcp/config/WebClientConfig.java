package com.manit.erp.mcp.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Spring WebClient configuration customized for communicating with MANIT ERP APIs.
 */
@Configuration
public class WebClientConfig {

    private static final Logger log = LoggerFactory.getLogger(WebClientConfig.class);

    private final ErpProperties erpProperties;

    public WebClientConfig(ErpProperties erpProperties) {
        this.erpProperties = erpProperties;
    }

    @Bean
    public WebClient erpWebClient(WebClient.Builder builder) {
        ErpProperties.Client clientProps = erpProperties.getClient();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientProps.getConnectTimeoutMs())
                .responseTimeout(Duration.ofMillis(clientProps.getReadTimeoutMs()))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(clientProps.getReadTimeoutMs(), TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(clientProps.getReadTimeoutMs(), TimeUnit.MILLISECONDS)));

        return builder
                .baseUrl(erpProperties.getApi().getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequestAndHeaders())
                .filter(logResponse())
                .build();
    }

    private ExchangeFilterFunction logRequestAndHeaders() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("Outgoing ERP Request: [{}] {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.debug("Incoming ERP Response Status: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}
