package com.manit.erp.mcp.config;

import io.modelcontextprotocol.common.McpTransportContext;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.server.transport.ServerTransportSecurityException;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Extracts and forwards the client's bearer header without validating the token itself.
 */
public final class McpBearerTokenSupport {

    public static final String AUTHORIZATION_CONTEXT_KEY = "authorization";

    private McpBearerTokenSupport() {
    }

    public static String authorizationHeader(@NonNull ToolContext toolContext) {
        McpSyncServerExchange exchange = McpToolUtils.getMcpExchange(toolContext)
                .orElseThrow(() -> new IllegalStateException("MCP HTTP request context is unavailable"));

        McpTransportContext transportContext = exchange.transportContext();
        Object authorizationHeader = transportContext.get(AUTHORIZATION_CONTEXT_KEY);
        if (!(authorizationHeader instanceof String header) || !hasBearerValue(header)) {
            throw new IllegalStateException("MCP request is missing a Bearer Authorization header");
        }
        return header;
    }

    static void validateBearerHeader(Map<String, List<String>> headers)
            throws ServerTransportSecurityException {
        List<String> authorizationValues = headers.entrySet()
                .stream()
                .filter(entry -> HttpHeaders.AUTHORIZATION.equalsIgnoreCase(entry.getKey()))
                .flatMap(entry -> entry.getValue().stream())
                .toList();

        if (authorizationValues.size() != 1 || !hasBearerValue(authorizationValues.get(0))) {
            throw new ServerTransportSecurityException(401,
                    "A Bearer Authorization header is required");
        }
    }

    private static boolean hasBearerValue(String header) {
        String[] parts = header.trim().split("\\s+", 2);
        return parts.length == 2 && "Bearer".equalsIgnoreCase(parts[0]) && !parts[1].isBlank();
    }
}
