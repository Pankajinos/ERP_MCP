package com.manit.erp.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main Spring Boot application bootstrap for the Student ERP MCP Server.
 */
@SpringBootApplication
@EnableConfigurationProperties
public class StudentErpMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentErpMcpApplication.class, args);
    }
}
