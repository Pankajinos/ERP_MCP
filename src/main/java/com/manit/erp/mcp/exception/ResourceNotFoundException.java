package com.manit.erp.mcp.exception;

/**
 * Thrown when requested subject, semester, or record is not found in ERP data.
 */
public class ResourceNotFoundException extends ErpApiException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
