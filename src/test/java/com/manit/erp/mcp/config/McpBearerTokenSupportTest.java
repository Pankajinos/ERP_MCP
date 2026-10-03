package com.manit.erp.mcp.config;

import io.modelcontextprotocol.common.McpTransportContext;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.server.transport.ServerTransportSecurityException;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class McpBearerTokenSupportTest {

    @Test
    void acceptsBearerHeaderWithoutVerifyingTokenContents() {
        assertDoesNotThrow(() -> McpBearerTokenSupport.validateBearerHeader(
                Map.of(HttpHeaders.AUTHORIZATION, List.of("Bearer opaque-token"))));
    }

    @Test
    void rejectsMissingOrMalformedBearerHeader() {
        assertThrows(ServerTransportSecurityException.class,
                () -> McpBearerTokenSupport.validateBearerHeader(Map.of()));
        assertThrows(ServerTransportSecurityException.class,
                () -> McpBearerTokenSupport.validateBearerHeader(
                        Map.of(HttpHeaders.AUTHORIZATION, List.of("Basic opaque-token"))));
        assertThrows(ServerTransportSecurityException.class,
                () -> McpBearerTokenSupport.validateBearerHeader(
                        Map.of(HttpHeaders.AUTHORIZATION, List.of("Bearer "))));
    }

    @Test
    void forwardsTheAuthorizationHeaderFromTheCurrentMcpRequest() {
        String authorizationHeader = "bEaReR opaque-token";
        McpSyncServerExchange exchange = mock(McpSyncServerExchange.class);
        when(exchange.transportContext()).thenReturn(McpTransportContext.create(
                Map.of(McpBearerTokenSupport.AUTHORIZATION_CONTEXT_KEY, authorizationHeader)));
        ToolContext toolContext = new ToolContext(
                Map.of(McpToolUtils.TOOL_CONTEXT_MCP_EXCHANGE_KEY, exchange));

        assertEquals(authorizationHeader, McpBearerTokenSupport.authorizationHeader(toolContext));
    }

    @Test
    void failsWhenTheMcpRequestContextHasNoAuthorizationHeader() {
        McpSyncServerExchange exchange = mock(McpSyncServerExchange.class);
        when(exchange.transportContext()).thenReturn(McpTransportContext.EMPTY);
        ToolContext toolContext = new ToolContext(
                Map.of(McpToolUtils.TOOL_CONTEXT_MCP_EXCHANGE_KEY, exchange));

        assertThrows(IllegalStateException.class,
                () -> McpBearerTokenSupport.authorizationHeader(toolContext));
    }
}
