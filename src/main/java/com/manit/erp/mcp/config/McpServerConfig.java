package com.manit.erp.mcp.config;

import com.manit.erp.mcp.tools.AcademicTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI MCP Server configuration registering business tools for MCP consumption.
 */
@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider academicToolsCallbackProvider(AcademicTools academicTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(academicTools)
                .build();
    }
}
