package com.manit.erp.mcp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manit.erp.mcp.tools.AcademicTools;
import io.modelcontextprotocol.common.McpTransportContext;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransportProvider;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerSseProperties;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.RouterFunction;

import java.util.Map;

/**
 * Spring AI MCP Server configuration registering business tools for MCP consumption.
 */
@Configuration
@EnableConfigurationProperties(McpServerSseProperties.class)
public class McpServerConfig {

    @Bean
    public WebFluxSseServerTransportProvider webFluxTransport(
            @Qualifier("mcpServerObjectMapper") ObjectMapper objectMapper,
            McpServerSseProperties serverProperties) {
        return WebFluxSseServerTransportProvider.builder()
                .jsonMapper(new JacksonMcpJsonMapper(objectMapper))
                .basePath(serverProperties.getBaseUrl())
                .messageEndpoint(serverProperties.getSseMessageEndpoint())
                .sseEndpoint(serverProperties.getSseEndpoint())
                .keepAliveInterval(serverProperties.getKeepAliveInterval())
                .contextExtractor(McpServerConfig::extractTransportContext)
                .securityValidator(McpBearerTokenSupport::validateBearerHeader)
                .build();
    }

    @Bean
    public RouterFunction<?> webfluxSseServerRouterFunction(
            WebFluxSseServerTransportProvider webFluxProvider) {
        return webFluxProvider.getRouterFunction();
    }

    @Bean
    public ToolCallbackProvider academicToolsCallbackProvider(AcademicTools academicTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(academicTools)
                .build();
    }

    private static McpTransportContext extractTransportContext(ServerRequest request) {
        return McpTransportContext.create(Map.of(
                McpBearerTokenSupport.AUTHORIZATION_CONTEXT_KEY,
                request.headers().firstHeader(HttpHeaders.AUTHORIZATION)));
    }

}
