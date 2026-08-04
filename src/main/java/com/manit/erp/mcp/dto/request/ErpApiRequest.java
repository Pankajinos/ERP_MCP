package com.manit.erp.mcp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Standard payload sent to ERP API endpoints.
 */
public record ErpApiRequest(
        @JsonProperty("studentuid") Integer studentuid,
        @JsonProperty("programID") Integer programID
) {}
