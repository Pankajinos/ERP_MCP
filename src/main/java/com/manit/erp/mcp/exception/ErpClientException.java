package com.manit.erp.mcp.exception;

/**
 * Thrown when HTTP WebClient communication with ERP backend fails.
 */
public class ErpClientException extends ErpApiException {
    public ErpClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
